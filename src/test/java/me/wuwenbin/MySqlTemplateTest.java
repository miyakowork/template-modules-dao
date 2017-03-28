package me.wuwenbin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/*
 * Created by wuwenbin on 2016/12/11.
 */
@SuppressWarnings("SpringContextConfigurationInspection")
@RunWith(SpringJUnit4ClassRunner.class)
//@Rollback(false)
@TransactionConfiguration(transactionManager = "remoteManager", defaultRollback = false)
@Transactional
@ContextConfiguration(locations = {"classpath:demo-config.xml"})
public class MySqlTemplateTest {

    @Autowired
    ServiceTest serviceTest;

    @Test
    public void test() throws Exception {
//        serviceTest.findAcglibraryUsers();
//        serviceTest.findCqsscUsers();
    serviceTest.testTransaction();
    }

}
