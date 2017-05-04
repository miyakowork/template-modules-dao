package org.templateproject.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.templateproject.dao.annotation.DynamicDataSource;
import org.templateproject.dao.factory.DaoFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by wuwenbin on 2017/5/4.
 */
@Service
public class TetService {

    @Autowired
    DaoFactory daoFactory;


    @DynamicDataSource("tp")
    public void findTP() {
        String sql = "select * from sys_users";
        List<Map<String, Object>> tps = daoFactory.dynamicDao.findListMapByArray(sql);
        for (Map<String, Object> tp : tps) {
            System.out.println(tp);
        }
    }

    @DynamicDataSource("s4")
    public void findS4() {
        String sql = "select * from uc_sys_resource";
        List<Map<String, Object>> s4s = daoFactory.dynamicDao.findListMapByArray(sql);
        for (Map<String, Object> s4 : s4s) {
            System.out.println(s4);
        }
    }
}
