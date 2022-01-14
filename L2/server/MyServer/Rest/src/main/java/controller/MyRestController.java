package controller;

import domain.Grade;
import domain.GradeCredentials;
import domain.Student;
import domain.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.IService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("api")
public class MyRestController {

    @Autowired
    private IService service;


    @GetMapping("/teachers")
    public Iterable<Teacher> getAllTeachers() {

        System.out.println("get all teachers");
        return service.getAllTeachers();

    }

    @GetMapping("/grades")
    public Iterable<Grade> getAllGrades() {

        System.out.println("get all grades");
        return service.getAllGrades();

    }

    @GetMapping("/students")
    public Iterable<Student> getAllStudents() {

        System.out.println("get all students");
        return service.getAllStudents();

    }

    @GetMapping("/grades/student/{id}")
    public List<Grade> getStudentsGrades(@PathVariable int id) {

        System.out.println("get studs grades: id ="+id);
        return service.getStudentsGrades(id);
    }

    @GetMapping("/grades/teacher?{studId}&{teacherId}&{grade}&{date}")
    public List<Grade> getTeachersGradesFiltered(@PathVariable int studId,
                                                 @PathVariable int teacherId,
                                                 @PathVariable int grade,
                                                 @PathVariable LocalDate date) {

        System.out.println("get all teachers grades filtered");
        return service.getTeachersGradesFiltered(studId, teacherId, grade, date);
    }

    @GetMapping("/grades/student?{studId}&{teacherId}&{grade}&{date}")
    public List<Grade> getStudentsGradesFiltered(@PathVariable int studId,
                                                 @PathVariable int teacherId,
                                                 @PathVariable int grade,
                                                 @PathVariable LocalDate date) {

        System.out.println("get all students grades filtered");
        return service.getStudentsGradesFiltered(studId, teacherId, grade, date);
    }

    @GetMapping("/grades/teacher/{id}")
    public List<Grade> getTeachersGrades(@PathVariable int id) {

        System.out.println("get all teachers grades");
        return service.getTeachersGrades(id);
    }

    @GetMapping("/teacher/by/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable int id) {

        System.out.println("get teacher by id");
        Teacher teacher = service.findOneTeacher(id);
        if(teacher!=null)
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        return new ResponseEntity<>("null", HttpStatus.NOT_FOUND);

    }

    @GetMapping("/student/by/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id) {

        System.out.println("get student by id");
        Student student= service.findOneStudent(id);
        System.out.println("student is "+student);
        if(student!=null)
            return new ResponseEntity<>(student, HttpStatus.OK);
        return new ResponseEntity<>("null", HttpStatus.NOT_FOUND);


    }

    @GetMapping("/student/{name}")
    public ResponseEntity<?> getStudentByName(@PathVariable String name) {

        System.out.println("get student by name");
        Student student=  service.findStudentByName(name);
        if(student!=null)
            return new ResponseEntity<>(student, HttpStatus.OK);
        return new ResponseEntity<>("null", HttpStatus.NOT_FOUND);

    }

    @GetMapping("/teacher/subject/{subject}")
    public ResponseEntity<?> getTeacherBySubject(@PathVariable String subject) {

        System.out.println("get teacher by subject "+subject);
        Teacher teacher = service.findTeacherBySubject(subject);
        System.out.println("teacher is "+teacher);
        if(teacher!=null)
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        return new ResponseEntity<>("null", HttpStatus.NOT_FOUND);

    }

    @GetMapping("/teacher/{name}")
    public Teacher getTeacherByName(@PathVariable String name) {

        System.out.println("get teacher by name");
        return service.findTeacherByName(name);

    }

    @PostMapping("/teacher")
    public ResponseEntity<?> getTeacherData(@RequestBody Map<String, String> values) {

        String em = values.get("email");
        String pass = values.get("password");
        Teacher teacher= service.getTeacherByData(em, pass);
        if(teacher!=null)
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        return new ResponseEntity<>("null", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/student")
    public ResponseEntity<?> getStudentData(@RequestBody Map<String, String> values) {

        String em = values.get("email");
        String pass = values.get("password");
        System.out.println("login stud with em "+em);
        Student stud=  service.getStudentByData(em, pass);
        System.out.println("sending back "+stud);
        if(stud!=null)
            return new ResponseEntity<>(stud, HttpStatus.OK);
        return new ResponseEntity<>("null", HttpStatus.NOT_FOUND);
    }


    @PutMapping("/grade")
    public ResponseEntity<?> update(@RequestBody GradeCredentials grade){
        System.out.println("update, with grade credentials "+grade);
        Grade e= new Grade(grade.getGradeValue(), grade.getStudentId(),grade.getTeacherId(),
                LocalDate.parse(grade.getDate()),grade.getChanged());
        e.setId(grade.getId());
        System.out.println("my grade: "+e);
        service.updateGrade(e);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @PostMapping("/grade")
    public ResponseEntity<?> addGrade(@RequestBody String text){

        Grade grade= decode(text);
        System.out.println("add grade "+grade);
        int myId = service.addGrade(grade);
        System.out.println("id to return "+myId);
        return new ResponseEntity<>(myId, HttpStatus.OK);

    }

    private Grade decode(String text){
        //studId=2&teacherId=1&grade=2&date=2019-08-10&changed=0
        Grade grade=new Grade();
        String[] values= text.split("&");
        if(values.length == 5) {

            //add-> there is no id
            int studId = Integer.parseInt(values[0].split("=")[1]);
            grade.setStudentId(studId);

            int teacherId = Integer.parseInt(values[1].split("=")[1]);
            grade.setTeacherId(teacherId);

            int gradeValue = Integer.parseInt(values[2].split("=")[1]);
            grade.setGradeValue(gradeValue);

            LocalDate date = LocalDate.parse(values[3].split("=")[1]);
            grade.setDate(date);

            int changed = Integer.parseInt(values[4].split("=")[1]);
            grade.setChanged(changed);

        }
        else{

            int id = Integer.parseInt(values[0].split("=")[1]);
            grade.setId(id);

            int studId = Integer.parseInt(values[1].split("=")[1]);
            grade.setStudentId(studId);

            int teacherId = Integer.parseInt(values[2].split("=")[1]);
            grade.setTeacherId(teacherId);

            int gradeValue = Integer.parseInt(values[3].split("=")[1]);
            grade.setGradeValue(gradeValue);

            LocalDate date = LocalDate.parse(values[4].split("=")[1]);
            grade.setDate(date);

            int changed = Integer.parseInt(values[5].split("=")[1]);
            grade.setChanged(changed);

        }
        return grade;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>  delete(@PathVariable int id) {
        service.deleteGrade(id);
        System.out.println("deleted");
        System.out.println(getAllGrades());
        return new ResponseEntity<>("OK", HttpStatus.OK);

    }



}
