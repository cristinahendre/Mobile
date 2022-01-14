package repository;

import domain.Grade;

import java.time.LocalDate;
import java.util.List;

public interface IGradeRepository extends IRepository<Integer, Grade> {

    int save(Grade grade);
    void update(Grade grade);
    void delete(int id);

    List<Grade> getStudentsGrades(int id);
    List<Grade> getTeachersGrades(int id);

    List<Grade> getStudentsGradesFiltered(int studId, int teacherId, int grade, LocalDate date);
    List<Grade> getTeachersGradesFiltered(int studId, int teacherId, int grade, LocalDate date);

}
