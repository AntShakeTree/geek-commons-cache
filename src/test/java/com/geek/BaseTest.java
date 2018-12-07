package com.geek;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-30 11:33
 */
@SpringBootConfiguration
@EnableAspectJAutoProxy
@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan
public class BaseTest {
    @Test
    public void Test(){
        Assert.assertEquals(true,true);
    }
}
