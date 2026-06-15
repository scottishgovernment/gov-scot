package scot.gov.publications;

/**
 * Configuration for this module, loaded by {@link PublicationsConfigurationLoader}. Mirrors the
 * shape of the old standalone importer's PublicationsConfiguration.S3 so the same fields/meaning
 * carry over, even though how it is loaded differs (see the loader for why).
 */
public class PublicationsConfiguration {

    private final S3 s3 = new S3();

    public S3 getS3() {
        return s3;
    }

    public static class S3 {

        private String bucket;

        private String key;

        private String secret;

        private String region;

        private String path = "publications";

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
