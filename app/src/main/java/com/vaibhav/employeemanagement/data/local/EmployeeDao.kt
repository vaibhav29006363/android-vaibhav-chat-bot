package com.vaibhav.employeemanagement.data.local

import androidx.room.*
import com.vaibhav.employeemanagement.data.model.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Long): Employee?

    @Query("SELECT * FROM employees WHERE department = :department ORDER BY name ASC")
    fun getEmployeesByDepartment(department: String): Flow<List<Employee>>

    @Query("SELECT DISTINCT department FROM employees ORDER BY department ASC")
    fun getAllDepartments(): Flow<List<String>>

    @Query("SELECT * FROM employees WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchEmployees(query: String): Flow<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteEmployeeById(id: Long)
}
