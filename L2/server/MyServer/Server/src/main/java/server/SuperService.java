package server;

import domain.Grade;
import domain.Student;
import domain.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.IGradeRepository;
import repository.IStudentRepository;
import repository.ITeacherRepository;
import service.IService;

import java.time.LocalDate;
import java.util.List;

@Service
public class SuperService implements IService {

    private final ITeacherRepository teacherRepository;
    private final IStudentRepository studentRepository;
    private final IGradeRepository gradeRepository;

    @Autowired
    public SuperService(ITeacherRepository teacherRepository,
                        IStudentRepository studentRepository,
                        IGradeRepository gradeRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.gradeRepository =gradeRepository;
    }

    @Override
    public Teacher getTeacherByData(String email, String pass) {
        return teacherRepository.findTeacherByEmailPassword(email, pass);
    }

    @Override
    public Teacher findOneTeacher(int id) {
        return teacherRepository.findOne(id);
    }

    @Override
    public Teacher findTeacherByName(String name) {
        return teacherRepository.getTeacherByName(name);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher findTeacherBySubject(String subject) {
        return teacherRepository.getTeacherBySubject(subject);
    }

    @Override
    public Student getStudentByData(String email, String pass) {
        return studentRepository.findStudentByEmailPassword(email, pass);
    }

    @Override
    public Student findOneStudent(int id) {
        return studentRepository.findOne(id);
    }

    @Override
    public Student findStudentByName(String name) {
        return studentRepository.getStudentByName(name);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public int addGrade(Grade gr) {
        return gradeRepository.save(gr);
    }

    @Override
    public void updateGrade(Grade grade) {
        gradeRepository.update(grade);
    }

    @Override
    public void deleteGrade(int gradeId) {
        gradeRepository.delete(gradeId);
    }

    @Override
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    @Override
    public List<Grade> getTeachersGrades(int id) {
        return gradeRepository.getTeachersGrades(id);
    }

    @Override
    public List<Grade> getStudentsGrades(int id) {
        return gradeRepository.getStudentsGrades(id);
    }

    @Override
    public List<Grade> getTeachersGradesFiltered(int studId, int teacherId, int grade, LocalDate date) {
        return gradeRepository.getTeachersGradesFiltered(studId, teacherId, grade, date);
    }

    @Override
    public List<Grade> getStudentsGradesFiltered(int studId, int teacherId, int grade, LocalDate date) {
        return gradeRepository.getStudentsGradesFiltered(studId, teacherId, grade, date);
    }
}
