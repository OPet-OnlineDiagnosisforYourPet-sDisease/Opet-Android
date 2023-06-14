package com.example.meowbottom.api

import com.example.meowbottom.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("stories?page=1&size=3")
    fun getAllStories(
        @Header("Authorization") token : String
    ) : Call<StoriesResponse>

    @GET("gejala")
    fun getAllSymptom(
    ) : Call<SymptomResponse>

    @FormUrlEncoded
    @POST("gejala")
    fun postSymptom(
        @Field("gejala") array: List<String>
    ) : Call<PostSymptomResponse>

    /*@FormUrlEncoded
    @POST("predict")
    fun checkSymptom(
        @Field("gejala") array: List<Int>
    ) : Call<CheckSymptomResponse>*/

    @FormUrlEncoded
    @POST("predict")
    fun checkSymptom(
        @Field("gejala") gejala: String
    ) : Call<CheckSymptomResponse>

    @GET("/penyakit/{id}")
    fun getDisease(
        @Path("id") id: String
    ) : Call<DiseaseResponse>

    @GET("article")
    fun getAllArticle(
    ) : Call<ArticleResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<RegisterResponse>

    @GET("stories")
    fun getAllCommunity(
        @Header("Authorization") token : String
    ) : Call<CommunityResponse>

    @GET("stories")
    fun getYourPost(
        @Query("email") email: String
    ) : Call<CommunityResponse>

    @GET("stories?location=1")
    fun getAllMaps(
        @Header("Authorization") token : String
    ) : Call<CommunityResponse>

    @GET("klinik")
    fun getAllMapsClinic(
    ) : Call<MapClinicResponse>



    @Multipart
    @POST("stories")
    fun postCommunity(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ) : Call<PostCommunityResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: Int
    ) : Call<DetailCommunityResponse>

    @Multipart
    @POST("stories/{id}/comment")
    fun postComment(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("comment") comment: RequestBody
    ) : Call<CommentResponse>

    @GET("stories")
    suspend fun getPageStories(
        @Header("Authorization") token : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : CommunityResponse

    @Multipart
    @POST("imageclassify")
    fun checkDiseaseCamera(
        @Part input: MultipartBody.Part,
    ) : Call<CameraDiseaseResponse>

    @Multipart
    @POST("profile/photo")
    fun updatePhotoProfile(
        @Header("Authorization") token : String,
        @Part photo: MultipartBody.Part,
    ) : Call<UpdateProfileResponse>

    @Multipart
    @POST("stories")
    fun postStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ) : Call<PostStoryResponse>

   /* @POST("predict")
    fun checkSymptom(@Body gejala: List<Int>) : Call<CheckSymptomResponse>*/

    /*//@FormUrlEncoded
    @POST("gejala")
    @Headers("Content-Type: application/json")
    fun postSymptom(
        @Field("gejala") array: List<String>
    ) : Call<SymptomResponse>*/

}