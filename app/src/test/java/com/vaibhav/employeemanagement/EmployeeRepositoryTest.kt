package com.vaibhav.employeemanagement

import com.vaibhav.employeemanagement.data.local.EmployeeDao
import com.vaibhav.employeemanagement.data.model.Employee
import com.vaibhav.employeemanagement.data.repository.EmployeeRepository
import com.vaibhav.employeemanagement.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmployeeRepositoryTest {

    private lateinit var employeeDao: EmployeeDao
    private lateinit var repository: EmployeeRepository

    private val testEmployee = Employee(
        id = 1,
        name = "John Doe",
        email = "john@example.com",
        department = "Engineering",
        role = "Software Engineer",
        hireDate = "2023-01-15"
    )

    @Before
    fun setUp() {
        employeeDao = mockk()
        repository = EmployeeRepository(employeeDao)
    }

    @Test
    fun `getAllEmployees returns success with employee list`() = runTest {
        val employees = listOf(testEmployee)
        coEvery { employeeDao.getAllEmployees() } returns flowOf(employees)

        val result = repository.getAllEmployees().first()

        assertTrue(result is Resource.Success)
        assertEquals(employees, (result as Resource.Success).data)
    }

    @Test
    fun `getEmployeesByDepartment returns filtered employees`() = runTest {
        val employees = listOf(testEmployee)
        coEvery { employeeDao.getEmployeesByDepartment("Engineering") } returns flowOf(employees)

        val result = repository.getEmployeesByDepartment("Engineering").first()

        assertTrue(result is Resource.Success)
        assertEquals(employees, (result as Resource.Success).data)
    }

    @Test
    fun `getEmployeeById returns success when employee exists`() = runTest {
        coEvery { employeeDao.getEmployeeById(1L) } returns testEmployee

        val result = repository.getEmployeeById(1L)

        assertTrue(result is Resource.Success)
        assertEquals(testEmployee, (result as Resource.Success).data)
    }

    @Test
    fun `getEmployeeById returns error when employee not found`() = runTest {
        coEvery { employeeDao.getEmployeeById(99L) } returns null

        val result = repository.getEmployeeById(99L)

        assertTrue(result is Resource.Error)
        assertEquals("Employee not found", (result as Resource.Error).message)
    }

    @Test
    fun `createEmployee returns success with new id`() = runTest {
        val newEmployee = testEmployee.copy(id = 0)
        coEvery { employeeDao.insertEmployee(newEmployee) } returns 1L

        val result = repository.createEmployee(newEmployee)

        assertTrue(result is Resource.Success)
        assertEquals(1L, (result as Resource.Success).data)
    }

    @Test
    fun `updateEmployee returns success on update`() = runTest {
        coEvery { employeeDao.updateEmployee(testEmployee) } returns Unit

        val result = repository.updateEmployee(testEmployee)

        assertTrue(result is Resource.Success)
        coVerify { employeeDao.updateEmployee(testEmployee) }
    }

    @Test
    fun `deleteEmployee returns success on deletion`() = runTest {
        coEvery { employeeDao.deleteEmployee(testEmployee) } returns Unit

        val result = repository.deleteEmployee(testEmployee)

        assertTrue(result is Resource.Success)
        coVerify { employeeDao.deleteEmployee(testEmployee) }
    }

    @Test
    fun `createEmployee returns error on database exception`() = runTest {
        val newEmployee = testEmployee.copy(id = 0)
        coEvery { employeeDao.insertEmployee(newEmployee) } throws RuntimeException("DB error")

        val result = repository.createEmployee(newEmployee)

        assertTrue(result is Resource.Error)
        assertEquals("DB error", (result as Resource.Error).message)
    }
}
