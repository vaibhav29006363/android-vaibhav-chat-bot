package com.vaibhav.employeemanagement.ui.employee

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vaibhav.employeemanagement.R
import com.vaibhav.employeemanagement.data.local.EmployeeDatabase
import com.vaibhav.employeemanagement.data.repository.EmployeeRepository
import com.vaibhav.employeemanagement.databinding.FragmentEmployeeDetailBinding
import com.vaibhav.employeemanagement.util.Resource

class EmployeeDetailFragment : Fragment() {

    private var _binding: FragmentEmployeeDetailBinding? = null
    private val binding get() = _binding!!

    private val args: EmployeeDetailFragmentArgs by navArgs()

    private val viewModel: EmployeeViewModel by viewModels {
        val db = EmployeeDatabase.getDatabase(requireContext())
        EmployeeViewModelFactory(EmployeeRepository(db.employeeDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayEmployeeDetails()
        setupMenu()
        observeOperationResult()
    }

    private fun displayEmployeeDetails() {
        val employee = args.employee
        binding.apply {
            textEmployeeId.text = getString(R.string.employee_id_format, employee.id)
            textName.text = employee.name
            textEmail.text = employee.email
            textDepartment.text = employee.department
            textRole.text = employee.role
            textHireDate.text = employee.hireDate
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_employee_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        val action = EmployeeDetailFragmentDirections
                            .actionEmployeeDetailFragmentToAddEditEmployeeFragment(args.employee)
                        findNavController().navigate(action)
                        true
                    }
                    R.id.action_delete -> {
                        showDeleteConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_employee)
            .setMessage(getString(R.string.delete_confirmation, args.employee.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteEmployee(args.employee)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeOperationResult() {
        viewModel.operationResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> findNavController().navigateUp()
                is Resource.Error -> Snackbar.make(binding.root, resource.message, Snackbar.LENGTH_LONG).show()
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
