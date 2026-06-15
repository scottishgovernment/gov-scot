package scot.gov.publications.storage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publications.PublicationsConfiguration;
import scot.gov.publications.PublicationsConfigurationLoader;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Archives every uploaded APS zip in S3, keyed by its checksum.  Can be used to retrieve the zip to
 * diagnose problems.
 */
public class S3PublicationStorage implements PublicationStorage {

    private static final Logger LOG = LoggerFactory.getLogger(S3PublicationStorage.class);

    @Override
    public boolean isConfigured() {
        return isNotBlank(configuration().getBucket());
    }

    /**
     * Upload the zip to S3 under a key derived from its checksum, unless S3 archiving is not
     * configured in this environment.
     */
    @Override
    public void save(String checksum, File file) throws PublicationStorageException {
        PublicationsConfiguration.S3 configuration = configuration();
        if (!isNotBlank(configuration.getBucket())) {
            LOG.info("S3 archiving not configured ({}), skipping S3 archive of uploaded zip", describe(configuration));
            return;
        }

        String key = key(configuration, checksum);
        try (S3Client client = buildClient(configuration)) {
            client.putObject(
                    PutObjectRequest.builder().bucket(configuration.getBucket()).key(key).build(),
                    RequestBody.fromFile(file));
            LOG.info("Archived uploaded zip in S3: {} key={}", describe(configuration), key);
        } catch (SdkException e) {
            throw new PublicationStorageException("Failed to archive zip in S3", e);
        }
    }

    /**
     * Download the previously archived zip for the given checksum to a fresh local temp file.
     */
    @Override
    public File fetch(String checksum) throws PublicationStorageException {
        PublicationsConfiguration.S3 configuration = configuration();
        String key = key(configuration, checksum);
        File file;
        try {
            file = File.createTempFile("publication-fetch-", ".zip");
        } catch (IOException e) {
            throw new PublicationStorageException("Failed to create temp file for S3 fetch", e);
        }
        // the AWS SDK's file transformer refuses to write to a file that already exists, so
        // only the (now-reserved) path is kept, not the empty placeholder file itself
        file.delete();
        try (S3Client client = buildClient(configuration)) {
            client.getObject(
                    GetObjectRequest.builder().bucket(configuration.getBucket()).key(key).build(),
                    ResponseTransformer.toFile(file.toPath()));
            return file;
        } catch (SdkException e) {
            // clean up any partially-written file left behind by the failed transfer, rather than
            // leaking it - the caller never gets a File reference to delete in this failure case
            FileUtils.deleteQuietly(file);
            throw new PublicationStorageException("Failed to fetch zip from S3 key=" + key, e);
        }
    }

    /**
     * No file extension - matches the old standalone importer's S3 key convention
     * (path(String).resolve(checksum)), so objects previously archived by that app and this one
     * are named identically.
     */
    private String key(PublicationsConfiguration.S3 configuration, String checksum) {
        return configuration.getPath() + "/" + checksum;
    }

    /**
     * Human-readable summary of the S3 connection settings in use, for logging - deliberately
     * omits the key/secret so they are never written to logs.
     */
    @Override
    public String describeConnection() {
        return describe(configuration());
    }

    private String describe(PublicationsConfiguration.S3 configuration) {
        return String.format(
                "bucket=%s path=%s region=%s credentials=%s",
                configuration.getBucket(),
                configuration.getPath(),
                isNotBlank(configuration.getRegion()) ? configuration.getRegion() : "(default provider chain)",
                isNotBlank(configuration.getKey()) ? "configured" : "(default provider chain)");
    }

    private PublicationsConfiguration.S3 configuration() {
        return PublicationsConfigurationLoader.load().getS3();
    }

    private S3Client buildClient(PublicationsConfiguration.S3 configuration) {
        return S3Client.builder()
                .credentialsProvider(credentialsProvider(configuration))
                .region(regionProvider(configuration).getRegion())
                .build();
    }

    private AwsCredentialsProvider credentialsProvider(PublicationsConfiguration.S3 configuration) {
        AwsCredentialsProvider configured = () -> {
            if (configuration.getKey() == null || configuration.getSecret() == null) {
                throw SdkClientException.create("No AWS credentials in configuration");
            }
            return AwsBasicCredentials.create(configuration.getKey(), configuration.getSecret());
        };
        return AwsCredentialsProviderChain.builder()
                .credentialsProviders(configured, DefaultCredentialsProvider.create())
                .build();
    }

    private AwsRegionProvider regionProvider(PublicationsConfiguration.S3 configuration) {
        AwsRegionProvider configured = () -> {
            if (configuration.getRegion() == null) {
                throw SdkClientException.create("No AWS region in configuration");
            }
            return Region.of(configuration.getRegion());
        };
        return new AwsRegionProviderChain(configured, new DefaultAwsRegionProviderChain());
    }
}
