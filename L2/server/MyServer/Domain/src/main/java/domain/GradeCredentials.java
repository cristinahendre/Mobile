package domain;

import java.io.Serializable;

public class GradeCredentials implements Serializable {

    private int id, gradeValue, studentId, teacherId, changed;
    private String date;

    public GradeCredentials(){ }


    @Override
    public String toString() {
        return "domain.Grade{" +
                "id=" + id +
                ", gradeValue=" + gradeValue +
                ", studentId=" + studentId +
                ", teacherId=" + teacherId +
                ", date=" + date +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(int gradeValue) {
        this.gradeValue = gradeValue;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }

    public GradeCredentials(int gradeValue, int studentId, int teacherId, String date, int changed) {
        this.gradeValue = gradeValue;
        this.studentId = studentId;
        this.changed= changed;
        this.teacherId = teacherId;
        this.date = date;
    }
}
