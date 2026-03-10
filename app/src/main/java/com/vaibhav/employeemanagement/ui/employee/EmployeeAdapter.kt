package com.vaibhav.employeemanagement.ui.employee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vaibhav.employeemanagement.data.model.Employee
import com.vaibhav.employeemanagement.databinding.ItemEmployeeBinding

class EmployeeAdapter(
    private val onEmployeeClick: (Employee) -> Unit,
    private val onEditClick: (Employee) -> Unit,
    private val onDeleteClick: (Employee) -> Unit
) : ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EmployeeViewHolder(
        private val binding: ItemEmployeeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee) {
            binding.apply {
                textEmployeeName.text = employee.name
                textEmployeeEmail.text = employee.email
                textEmployeeDepartment.text = employee.department
                textEmployeeRole.text = employee.role

                root.setOnClickListener { onEmployeeClick(employee) }
                buttonEdit.setOnClickListener { onEditClick(employee) }
                buttonDelete.setOnClickListener { onDeleteClick(employee) }
            }
        }
    }

    class EmployeeDiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}
