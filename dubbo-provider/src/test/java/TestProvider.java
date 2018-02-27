import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by dafu on 2018/2/25.
 */
public class TestProvider {

    @Test
    public void testProvider(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-servlet-provider.xml");
        context.start();

        System.out.println("Dubbo provider start...");

        try {
            System.in.read();   // 按任意键退出
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
