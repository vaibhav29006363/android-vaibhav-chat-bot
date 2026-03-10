package com.vaibhav.employeemanagement.data.remote

import com.vaibhav.employeemanagement.data.model.Employee
import retrofit2.Response
import retrofit2.http.*

/**
 * RESTful API interface for Employee operations.
 * Follows REST conventions: GET, POST, PUT, DELETE.
 */
interface EmployeeApiService {

    @GET("employees")
    suspend fun getAllEmployees(): Response<List<Employee>>

    @GET("employees/{id}")
    suspend fun getEmployeeById(@Path("id") id: Long): Response<Employee>

    @GET("employees")
    suspend fun getEmployeesByDepartment(
        @Query("department") department: String
    ): Response<List<Employee>>

    @POST("employees")
    suspend fun createEmployee(@Body employee: Employee): Response<Employee>

    @PUT("employees/{id}")
    suspend fun updateEmployee(
        @Path("id") id: Long,
        @Body employee: Employee
    ): Response<Employee>

    @DELETE("employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: Long): Response<Unit>
}
