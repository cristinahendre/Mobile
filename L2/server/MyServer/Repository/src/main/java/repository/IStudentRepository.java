package repository;

import domain.Student;
import domain.Teacher;

public interface IStudentRepository extends IRepository<Integer, Student> {
    Student findStudentByEmailPassword(String em, String pass);

    Student getStudentByName(String name);
}
