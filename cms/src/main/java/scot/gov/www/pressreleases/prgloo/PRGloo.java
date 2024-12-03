package scot.gov.www.pressreleases.prgloo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import scot.gov.www.pressreleases.prgloo.rest.ChangeHistory;
import scot.gov.www.pressreleases.prgloo.rest.PressReleaseItem;
import scot.gov.www.pressreleases.prgloo.rest.TagGroups;

import java.time.Instant;

public interface PRGloo {

    @GET("content/{id}")
    Call<PressReleaseItem> item(@Path("id") String id);

    @GET("content/changes?contentStreamId=57c7f3b406a210103429f644")
    Call<ChangeHistory> changesNews(@Query("startDateTime") Instant time);

    @GET("content/changes?contentStreamId=57c7f3b406a210103429f646")
    Call<ChangeHistory> changesSpeeches(@Query("startDateTime") Instant time);

    @GET("content/changes?contentStreamId=6369117cf675a15621def57c")
    Call<ChangeHistory> changesCorrespondences(@Query("startDateTime") Instant time);

    @GET("business")
    Call<TagGroups> tagGroups();

}
