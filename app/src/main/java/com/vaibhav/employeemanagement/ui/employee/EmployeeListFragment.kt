package com.vaibhav.employeemanagement.ui.employee

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vaibhav.employeemanagement.R
import com.vaibhav.employeemanagement.data.local.EmployeeDatabase
import com.vaibhav.employeemanagement.data.model.Employee
import com.vaibhav.employeemanagement.data.repository.EmployeeRepository
import com.vaibhav.employeemanagement.databinding.FragmentEmployeeListBinding
import com.vaibhav.employeemanagement.util.Resource
import kotlinx.coroutines.launch

class EmployeeListFragment : Fragment() {

    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EmployeeViewModel by viewModels {
        val db = EmployeeDatabase.getDatabase(requireContext())
        EmployeeViewModelFactory(EmployeeRepository(db.employeeDao()))
    }

    private lateinit var adapter: EmployeeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        setupDepartmentFilter()
        setupMenu()
        observeEmployees()
        observeOperationResult()
        observeDepartments()
    }

    private fun setupRecyclerView() {
        adapter = EmployeeAdapter(
            onEmployeeClick = { employee ->
                val action = EmployeeListFragmentDirections
                    .actionEmployeeListFragmentToEmployeeDetailFragment(employee)
                findNavController().navigate(action)
            },
            onEditClick = { employee ->
                val action = EmployeeListFragmentDirections
                    .actionEmployeeListFragmentToAddEditEmployeeFragment(employee)
                findNavController().navigate(action)
            },
            onDeleteClick = { employee ->
                showDeleteConfirmationDialog(employee)
            }
        )
        binding.recyclerViewEmployees.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddEmployee.setOnClickListener {
            val action = EmployeeListFragmentDirections
                .actionEmployeeListFragmentToAddEditEmployeeFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun setupDepartmentFilter() {
        binding.spinnerDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position) as String
                viewModel.setDepartmentFilter(if (selected == getString(R.string.all_departments)) null else selected)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_employee_list, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setSearchQuery(newText ?: "")
                        return true
                    }
                })
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeEmployees() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.employees.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.textEmptyState.isVisible = false
                        }
                        is Resource.Success -> {
                            binding.progressBar.isVisible = false
                            val employees = resource.data
                            adapter.submitList(employees)
                            binding.textEmptyState.isVisible = employees.isEmpty()
                        }
                        is Resource.Error -> {
                            binding.progressBar.isVisible = false
                            showSnackbar(resource.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeDepartments() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.departments.collect { resource ->
                    if (resource is Resource.Success) {
                        val departments = mutableListOf(getString(R.string.all_departments))
                        departments.addAll(resource.data)
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            departments
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerDepartment.adapter = adapter
                    }
                }
            }
        }
    }

    private fun observeOperationResult() {
        viewModel.operationResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> showSnackbar(getString(R.string.operation_successful))
                is Resource.Error -> showSnackbar(resource.message)
                else -> {}
            }
        }
    }

    private fun showDeleteConfirmationDialog(employee: Employee) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_employee)
            .setMessage(getString(R.string.delete_confirmation, employee.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteEmployee(employee)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
