package com.vaibhav.employeemanagement.ui.employee

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.vaibhav.employeemanagement.R
import com.vaibhav.employeemanagement.data.local.EmployeeDatabase
import com.vaibhav.employeemanagement.data.model.Employee
import com.vaibhav.employeemanagement.data.repository.EmployeeRepository
import com.vaibhav.employeemanagement.databinding.FragmentAddEditEmployeeBinding
import com.vaibhav.employeemanagement.util.Resource
import java.text.SimpleDateFormat
import java.util.*

class AddEditEmployeeFragment : Fragment() {

    private var _binding: FragmentAddEditEmployeeBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditEmployeeFragmentArgs by navArgs()

    private val viewModel: EmployeeViewModel by viewModels {
        val db = EmployeeDatabase.getDatabase(requireContext())
        EmployeeViewModelFactory(EmployeeRepository(db.employeeDao()))
    }

    private val isEditMode get() = args.employee != null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditEmployeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarTitle()
        populateFieldsIfEditing()
        setupDatePicker()
        setupSaveButton()
        setupMenu()
        observeOperationResult()
    }

    private fun setupToolbarTitle() {
        val title = if (isEditMode) getString(R.string.edit_employee) else getString(R.string.add_employee)
        requireActivity().title = title
    }

    private fun populateFieldsIfEditing() {
        args.employee?.let { employee ->
            binding.apply {
                editTextName.setText(employee.name)
                editTextEmail.setText(employee.email)
                editTextDepartment.setText(employee.department)
                editTextRole.setText(employee.role)
                editTextHireDate.setText(employee.hireDate)
            }
        }
    }

    private fun setupDatePicker() {
        binding.editTextHireDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            binding.editTextHireDate.text?.toString()?.takeIf { it.isNotBlank() }?.let { existing ->
                runCatching { dateFormat.parse(existing)?.let { calendar.time = it } }
            }

            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.editTextHireDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (validateInputs()) {
                saveEmployee()
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == android.R.id.home) {
                    findNavController().navigateUp()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            var isValid = true

            if (editTextName.text.isNullOrBlank()) {
                textInputLayoutName.error = getString(R.string.error_name_required)
                isValid = false
            } else {
                textInputLayoutName.error = null
            }

            if (editTextEmail.text.isNullOrBlank()) {
                textInputLayoutEmail.error = getString(R.string.error_email_required)
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString()).matches()) {
                textInputLayoutEmail.error = getString(R.string.error_invalid_email)
                isValid = false
            } else {
                textInputLayoutEmail.error = null
            }

            if (editTextDepartment.text.isNullOrBlank()) {
                textInputLayoutDepartment.error = getString(R.string.error_department_required)
                isValid = false
            } else {
                textInputLayoutDepartment.error = null
            }

            if (editTextRole.text.isNullOrBlank()) {
                textInputLayoutRole.error = getString(R.string.error_role_required)
                isValid = false
            } else {
                textInputLayoutRole.error = null
            }

            if (editTextHireDate.text.isNullOrBlank()) {
                textInputLayoutHireDate.error = getString(R.string.error_hire_date_required)
                isValid = false
            } else {
                textInputLayoutHireDate.error = null
            }

            return isValid
        }
    }

    private fun saveEmployee() {
        binding.apply {
            val employee = Employee(
                id = args.employee?.id ?: 0,
                name = editTextName.text.toString().trim(),
                email = editTextEmail.text.toString().trim(),
                department = editTextDepartment.text.toString().trim(),
                role = editTextRole.text.toString().trim(),
                hireDate = editTextHireDate.text.toString().trim()
            )

            if (isEditMode) {
                viewModel.updateEmployee(employee)
            } else {
                viewModel.createEmployee(employee)
            }
        }
    }

    private fun observeOperationResult() {
        viewModel.operationResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
