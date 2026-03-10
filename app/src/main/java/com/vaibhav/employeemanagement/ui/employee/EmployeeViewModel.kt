package com.vaibhav.employeemanagement.ui.employee

import androidx.lifecycle.*
import com.vaibhav.employeemanagement.data.model.Employee
import com.vaibhav.employeemanagement.data.repository.EmployeeRepository
import com.vaibhav.employeemanagement.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _selectedDepartment = MutableStateFlow<String?>(null)
    val selectedDepartment: StateFlow<String?> = _selectedDepartment.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val employees: StateFlow<Resource<List<Employee>>> = combine(
        _selectedDepartment,
        _searchQuery
    ) { department, query -> Pair(department, query) }
        .flatMapLatest { (department, query) ->
            when {
                query.isNotBlank() -> repository.searchEmployees(query)
                !department.isNullOrBlank() -> repository.getEmployeesByDepartment(department)
                else -> repository.getAllEmployees()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )

    val departments: StateFlow<Resource<List<String>>> = repository.getAllDepartments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )

    private val _operationResult = MutableLiveData<Resource<Unit>>()
    val operationResult: LiveData<Resource<Unit>> = _operationResult

    fun setDepartmentFilter(department: String?) {
        _selectedDepartment.value = department
        _searchQuery.value = ""
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _selectedDepartment.value = null
    }

    fun createEmployee(employee: Employee) {
        viewModelScope.launch {
            _operationResult.value = Resource.Loading
            val result = repository.createEmployee(employee)
            _operationResult.value = when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> Resource.Error(result.message)
                Resource.Loading -> Resource.Loading
            }
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            _operationResult.value = Resource.Loading
            _operationResult.value = repository.updateEmployee(employee)
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            _operationResult.value = Resource.Loading
            _operationResult.value = repository.deleteEmployee(employee)
        }
    }
}
