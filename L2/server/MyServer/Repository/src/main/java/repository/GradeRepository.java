package repository;

import domain.Grade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class GradeRepository implements IGradeRepository{

    List<Grade> allGrades= new ArrayList<>();
    int latestId;

    @Autowired
    public GradeRepository(){
        populate();
        latestId  =getLatestId();
    }
    @Override
    public int save(Grade grade) {

        grade.setId(latestId);
        latestId++;
        allGrades.add(grade);
        System.out.println(allGrades);
        return latestId -1;
    }

    @Override
    public void update(Grade grade) {
        Grade myGrade= findOne(grade.getId());
        if(myGrade == null){
            //data was not updated
            allGrades.add(grade);
        }
        else {
            myGrade.setGradeValue(grade.getGradeValue());
            myGrade.setDate(grade.getDate());
            myGrade.setStudentId(grade.getStudentId());
            myGrade.setTeacherId(grade.getTeacherId());
            System.out.println("the grade updated is: "+myGrade);
        }
    }

    @Override
    public void delete(int id) {
        Grade myGrade=findOne(id);
        if(myGrade != null) {
            allGrades.remove(myGrade);

        }
    }

    @Override
    public List<Grade> getStudentsGrades(int id) {

        List<Grade> grades =new ArrayList<>();
        for(Grade gr:allGrades){
            if(gr.getStudentId() == id) grades.add(gr);
        }
        return  grades;
    }

    @Override
    public List<Grade> getTeachersGrades(int id) {
        List<Grade> grades =new ArrayList<>();
        for(Grade gr:allGrades){
            if(gr.getTeacherId() == id) grades.add(gr);
        }
        return  grades;
    }

    @Override
    public List<Grade> getStudentsGradesFiltered(int studId, int teacherId, int grade, LocalDate date) {
        List<Grade> grades =new ArrayList<>();
        for(Grade gr:allGrades){
            if(gr.getStudentId() == studId && (gr.getTeacherId() == teacherId ||
                    gr.getGradeValue() == grade || gr.getDate().equals(date)))
                grades.add(gr);
        }
        return  grades;
    }

    @Override
    public List<Grade> getTeachersGradesFiltered(int studId, int teacherId, int grade, LocalDate date) {
        List<Grade> grades =new ArrayList<>();
        for(Grade gr:allGrades){
            if(gr.getTeacherId() == teacherId && (gr.getStudentId() == studId ||
                    gr.getGradeValue() == grade || gr.getDate().equals(date)))
                grades.add(gr);
        }
        return  grades;
    }

    @Override
    public Grade findOne(Integer integer) {
        for(Grade gr: allGrades){
            if(gr.getId() == integer) return  gr;
        }
        return null;
    }

    private  int getLatestId(){
        int size=allGrades.size();
        return allGrades.get(size-1).getId()+2;
    }

    @Override
    public List<Grade> findAll() {
        return allGrades;
    }

    private void populate(){
        Grade gr1= new Grade(10, 1, 1, LocalDate.parse("2020-01-10"),0);
        gr1.setId(1);
        allGrades.add(gr1);

        Grade gr2= new Grade(8,2,2,LocalDate.parse("2020-01-10"),0);
        gr2.setId(2);
        allGrades.add(gr2);

        Grade gr3= new Grade(3,3,3,LocalDate.parse("2021-11-10"),0);
        gr3.setId(3);
        allGrades.add(gr3);

        Grade gr4= new Grade(9,3,1,LocalDate.parse("2020-04-18"),0);
        gr4.setId(4);
        allGrades.add(gr4);
    }
}
