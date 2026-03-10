package com.vaibhav.employeemanagement.data.repository

import com.vaibhav.employeemanagement.data.local.EmployeeDao
import com.vaibhav.employeemanagement.data.model.Employee
import com.vaibhav.employeemanagement.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class EmployeeRepository(private val employeeDao: EmployeeDao) {

    fun getAllEmployees(): Flow<Resource<List<Employee>>> {
        return employeeDao.getAllEmployees()
            .map { Resource.Success(it) as Resource<List<Employee>> }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    fun getEmployeesByDepartment(department: String): Flow<Resource<List<Employee>>> {
        return employeeDao.getEmployeesByDepartment(department)
            .map { Resource.Success(it) as Resource<List<Employee>> }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    fun getAllDepartments(): Flow<Resource<List<String>>> {
        return employeeDao.getAllDepartments()
            .map { Resource.Success(it) as Resource<List<String>> }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    fun searchEmployees(query: String): Flow<Resource<List<Employee>>> {
        return employeeDao.searchEmployees(query)
            .map { Resource.Success(it) as Resource<List<Employee>> }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    suspend fun getEmployeeById(id: Long): Resource<Employee> {
        return try {
            val employee = employeeDao.getEmployeeById(id)
            if (employee != null) {
                Resource.Success(employee)
            } else {
                Resource.Error("Employee not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun createEmployee(employee: Employee): Resource<Long> {
        return try {
            val id = employeeDao.insertEmployee(employee)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create employee")
        }
    }

    suspend fun updateEmployee(employee: Employee): Resource<Unit> {
        return try {
            employeeDao.updateEmployee(employee)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update employee")
        }
    }

    suspend fun deleteEmployee(employee: Employee): Resource<Unit> {
        return try {
            employeeDao.deleteEmployee(employee)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete employee")
        }
    }
}
