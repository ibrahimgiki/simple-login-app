package com.trickyworld.simpleloginapp;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserServiceInterface {
   @FormUrlEncoded
   @POST("login.php")
   Call<JsonObject> login(
     @Field("username") String username,
     @Field("password") String password
   );
}
