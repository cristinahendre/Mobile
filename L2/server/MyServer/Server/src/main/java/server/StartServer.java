package server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import repository.TeacherRepository;

public class StartServer {
    public static void main(String[] args) {
        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-server.xml");

        TeacherRepository repo= new TeacherRepository();
        System.out.println( repo.findTeacherByEmailPassword("a@prof.com","a"));
    }
}
