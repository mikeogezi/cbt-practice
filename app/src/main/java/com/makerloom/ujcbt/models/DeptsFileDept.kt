package com.makerloom.ujcbt.models

data class DeptsFileDept (val name: String, val shortName: String, val faculty: String,
                     val courses: List<DeptsFileCourse>) {}