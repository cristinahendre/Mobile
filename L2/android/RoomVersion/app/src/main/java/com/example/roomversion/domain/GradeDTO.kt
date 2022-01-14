package com.example.roomversion.domain
import java.time.LocalDate

data class GradeDTO(
    var GradeId: Int,
    var TeacherName: String,
    var StudentName: String,
    var GradeValue:Int,
    var Date: LocalDate
){
    constructor(studentName:String, grade:Int, date:LocalDate):
            this(-1,"ha",studentName,grade,date)
}