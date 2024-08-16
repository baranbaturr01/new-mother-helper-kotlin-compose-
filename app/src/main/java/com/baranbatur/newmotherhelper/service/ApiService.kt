package com.baranbatur.newmotherhelper.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val data: TokenData)
data class RegisterRequest(
    val name: String, val surname: String, val email: String, val password: String
)

data class UpdateCategoryItemRequest(val categoryListId: Int)
data class RegisterResponse(val success: Boolean, val data: RegisterData)
data class RegisterData(val name: String, val surname: String, val email: String, val token: String)
data class TokenData(val token: String)
data class Category(val id: Int, val name: String, val description: String)
data class CategoryResponse(val success: Boolean, val data: List<Category>)
data class CategoryListResponse(val success: Boolean, val data: CategoryListData)
data class CategoryListData(
    val id: Int,
    val categoryName: String,
    val description: String,
    val items: List<CategoryListItem>
)

data class UpdateCategoryItemResponse(val success: Boolean, val data: UpdateCategoryItemData)
data class UpdateCategoryItemData(
    val categoryId: Int, val itemName: String, val added: Boolean
)

data class CategoryListItem(
    val id: Int, val itemName: String, val is_added: Boolean, val iconUrl: String
)

data class UserCategoryListResponse(
    val success: Boolean, val data: List<UserCategoryListData>
)

data class UserCategoryListData(
    val id: Int,
    val categoryId: Int,
    val categoryName: String,
    val itemName: String,
    val iconUrl: String,
)

data class DeleteUserCategoryListResponse(val success: Boolean)
data class ContentResponse(val success: Boolean, val data: List<ContentData>)
data class ContentData(
    val id: Int, val title: String, val description: String, val imageUrl: String
)

interface ApiService {
    @POST("api/v1/user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/v1/user/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("api/v1/category")
    fun getCategories(@Header("Authorization") token: String): Call<CategoryResponse>

    @GET("api/v1/category-list/find-by-category-id")
    fun getCategoryList(
        @Header("Authorization") token: String, @Query("categoryId") categoryId: Int
    ): Call<CategoryListResponse>

    @POST("api/v1/user-category-list")
    fun updateCategoryItem(
        @Header("Authorization") token: String, @Body request: UpdateCategoryItemRequest
    ): Call<UpdateCategoryItemResponse>

    @GET("api/v1/user-category-list")
    fun getUserCategoryList(@Header("Authorization") token: String): Call<UserCategoryListResponse>

    @DELETE("api/v1/user-category-list/{id}")
    fun deleteUserCategoryList(
        @Header("Authorization") token: String, @Path("id") id: Int
    ): Call<DeleteUserCategoryListResponse>

    @GET("api/v1/contents")
    fun getContent(@Header("Authorization") token: String): Call<ContentResponse>
}