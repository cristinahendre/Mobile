package service;

import domain.Grade;
import domain.Student;
import domain.Teacher;

import java.time.LocalDate;
import java.util.List;

public interface IService {

    Teacher getTeacherByData(String email, String pass);
    Teacher findOneTeacher(int id);
    Teacher findTeacherByName(String name);
    List<Teacher> getAllTeachers();
    Teacher findTeacherBySubject(String subject);

    Student getStudentByData(String email, String pass);
    Student findOneStudent(int id);
    Student findStudentByName(String name);
    List<Student> getAllStudents();

    int addGrade(Grade gr);
    void updateGrade(Grade grade);
    void deleteGrade(int gradeId);
    List<Grade> getAllGrades();
    List<Grade> getTeachersGrades(int id);
    List<Grade> getStudentsGrades(int id);
    List<Grade> getTeachersGradesFiltered(int studId, int teacherId, int grade, LocalDate date);
    List<Grade> getStudentsGradesFiltered(int studId, int teacherId, int grade, LocalDate date);
}
