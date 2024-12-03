package scot.gov.www.pressreleases.prgloo;

import retrofit2.Call;
import retrofit2.Response;
import scot.gov.www.pressreleases.PressReleaseImporterException;

import java.io.IOException;

public class Calls {

    public static <T> Response<T> call(Call<T> call) {
        Response<T> response;
        try {
            response = call.execute();
        } catch (IOException ex) {
            String message = "Request failed: " + call.request().url();
            throw new PressReleaseImporterException(message, ex);
        }
        return response;
    }
}
