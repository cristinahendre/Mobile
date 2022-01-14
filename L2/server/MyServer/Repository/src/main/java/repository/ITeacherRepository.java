package repository;

import domain.Teacher;

public interface ITeacherRepository extends  IRepository<Integer, Teacher>{

    Teacher findTeacherByEmailPassword(String em, String pass);

    Teacher getTeacherByName(String name);

    Teacher getTeacherBySubject(String subject);
}
