package repository;


import domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentRepository implements IStudentRepository{

    List<Student> allStudents;

    @Autowired
    public StudentRepository(){
        allStudents = new ArrayList<>();
        populate();

    }

    @Override
    public Student findOne(Integer integer) {
       for(Student stud: allStudents){
           if (stud.getId() == integer) return stud;
       }

       return null;
    }

    @Override
    public List<Student> findAll() {
        return  allStudents;
    }

    @Override
    public Student findStudentByEmailPassword(String email, String pass) {

        for(Student t: allStudents){
            if(t.getPassword().equals(pass) && t.getEmail().equals(email))
                return  t;
        }
        return  null;
    }

    @Override
    public Student getStudentByName(String name) {
        for(Student t: allStudents){
            if(t.getName().equals(name))
                return  t;
        }
        return  null;
    }

    private void populate(){

        Student Student= new Student("a","a@elev.com","a");
        Student.setId(1);
        allStudents.add(Student);

        Student stud1 = new Student();
        stud1.setId(2);
        stud1.setEmail("c@elev.com");
        stud1.setName("c");
        stud1.setPassword("c");
        allStudents.add(stud1);

        Student stud2 = new Student();
        stud2.setId(3);
        stud2.setEmail("b@elev.com");
        stud2.setName("b");
        stud2.setPassword("b");
        allStudents.add(stud2);
    }
}

