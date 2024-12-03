package scot.gov.www.pressreleases.prgloo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class PRGlooFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PRGlooFactory.class);

    public static final String SPEECH_STREAM_ID = "57c7f3b406a210103429f646";

    public static final String PRESS_RELEASE_STREAM_ID = "57c7f3b406a210103429f644";

    public static final String CORRESPONDENCE_STREAM_ID = "6369117cf675a15621def57c";

    private PRGlooFactory() {
        // Should not be instantiated.
    }

    public static PRGloo newInstance() {
        PRGlooConfiguration config = config();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("X-Account-Id", config.getToken())
                            .build();
                    LOGGER.info("Requesting: {}", request.url());
                    return chain.proceed(request);
                })
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonConverterFactory jackson = JacksonConverterFactory.create(mapper);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getApi())
                .client(client)
                .addConverterFactory(jackson)
                .build();
        return retrofit.create(PRGloo.class);
    }

    public static PRGlooConfiguration config() {
        ContainerConfiguration containerConfiguration = HstServices.getComponentManager().getContainerConfiguration();
        String token = containerConfiguration.getString("prgloo.token");
        if (token == null) {
            return null;
        }

        PRGlooConfiguration configuration = new PRGlooConfiguration();
        configuration.setToken(token);
        return configuration;
    }
}