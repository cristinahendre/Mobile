package repository;

import domain.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeacherRepository implements ITeacherRepository{

    List<Teacher> allTeachers;

    @Autowired
    public TeacherRepository(){
        allTeachers = new ArrayList<>();
        populate();

    }

    @Override
    public Teacher findOne(Integer integer) {

        for(Teacher teacher: allTeachers){
            if(teacher.getId() == integer) return  teacher;
        }
        return null;
    }

    @Override
    public List<Teacher> findAll() {
        return  allTeachers;
    }

    @Override
    public Teacher findTeacherByEmailPassword(String email, String pass) {

        for(Teacher t: allTeachers){
            if(t.getPassword().equals(pass) && t.getEmail().equals(email))
                return  t;
        }
        return  null;
    }

    @Override
    public Teacher getTeacherByName(String name) {
        for(Teacher t: allTeachers){
            if(t.getName().equals(name))
                return  t;
        }
        return  null;
    }

    @Override
    public Teacher getTeacherBySubject(String subject) {
        for(Teacher t: allTeachers){
            if(t.getSubject().equals(subject)) return  t;
        }

        return  null;
    }

    private void populate(){

        Teacher teacher= new Teacher("a","a@prof.com","a","Maths");
        teacher.setId(1);
        allTeachers.add(teacher);

        Teacher teacher1 = new Teacher();
        teacher1.setId(2);
        teacher1.setEmail("c@prof.com");
        teacher1.setName("c");
        teacher1.setPassword("c");
        teacher1.setSubject("Reading");
        allTeachers.add(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setId(3);
        teacher2.setEmail("b@prof.com");
        teacher2.setName("b");
        teacher2.setPassword("b");
        teacher2.setSubject("Science");
        allTeachers.add(teacher2);
    }
}
