package com.baranbatur.newmotherhelper.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Query

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val data: TokenData)
data class RegisterRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String
)

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

data class CategoryListItem(val id: Int, val itemName: String, val is_added: Boolean)
//bunlar model adında bir paket altında toplanabilir TODO

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
}