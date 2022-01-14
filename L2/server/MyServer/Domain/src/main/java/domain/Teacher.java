package domain;

import java.io.Serializable;

public class Teacher implements Serializable {

    private int id;
    private String name,email,password, subject;

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Teacher(){}

    public Teacher(String name, String email, String password, String subject) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "domain.Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
