package com.vaibhav.employeemanagement.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "department")
    val department: String,

    @ColumnInfo(name = "role")
    val role: String,

    @ColumnInfo(name = "hire_date")
    val hireDate: String
) : Parcelable
