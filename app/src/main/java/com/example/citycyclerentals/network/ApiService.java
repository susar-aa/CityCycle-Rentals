package com.example.citycyclerentals.network;

import com.example.citycyclerentals.models.Reservation;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("get_ongoing_activities.php")
    Call<List<Reservation>> getOngoingActivities(@Query("user_id") int userId);
}