import com.dubbo.demo.service.DemoService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by dafu on 2018/2/25.
 */
public class TestConsumer {

    @Test
    public void testConsumer(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-servlet-consumer.xml");
        context.start();

        DemoService demoService = (DemoService) context.getBean("demoService");

        System.out.println(demoService.sayHello("哈哈哈"));
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
