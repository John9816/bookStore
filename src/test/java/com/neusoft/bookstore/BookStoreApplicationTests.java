package com.neusoft.bookstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookStoreApplicationTests {

    @Test
    public void contextLoads() {
        int a = 10;
        int b = 20;
        System.out.println(a + b+"");
        System.out.println(""+a + b);
    }

}
