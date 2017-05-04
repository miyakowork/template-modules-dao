package org.templateproject.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wuwenbin on 2017/5/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:demo.xml"})
public class DaoTest {

    @Autowired
    TetService tetService;

    @Test
    public void test(){

        tetService.findS4();
        tetService.findTP();
    }

}
