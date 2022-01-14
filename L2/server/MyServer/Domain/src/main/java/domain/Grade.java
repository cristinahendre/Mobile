package domain;

import java.io.Serializable;
import java.time.LocalDate;

public class Grade implements Serializable {

    private int id, gradeValue, studentId, teacherId, changed;
    private LocalDate date;

    public Grade(){ }

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }

    public Grade(int gradeValue, int studentId, int teacherId, LocalDate date, int changed) {
        this.gradeValue = gradeValue;
        this.studentId = studentId;
        this.changed= changed;
        this.teacherId = teacherId;
        this.date = date;
    }
}
