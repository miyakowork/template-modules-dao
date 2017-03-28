package me.wuwebin.dao.test;

import com.sun.tools.jdi.LinkedHashMap;
import me.wuwenbin.dao.annotation.DynamicDataSource;
import me.wuwenbin.dao.factory.DaoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by wuwenbin on 2017/3/27.
 */
@Service
public class ServiceTest {

    @Autowired
    private DaoFactory daoFactory;

    public void findAcglibraryUsers() {
        List<Map<String, Object>> list = daoFactory.dynamicDao.findListMapByArray("select * from t_system_user");
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }
    }

    @DynamicDataSource("remote")
    public void findCqsscUsers() {
        List<Map<String, Object>> list = daoFactory.dynamicDao.findListMapByArray("select * from t_user");
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }
    }

    @Transactional(value = "locationManager")
    public void testTransaction() throws Exception {
        String updateSQL = "update t_system_user set user_name = :userName where  user_id = :userId";
        Map<String, Object> map = new LinkedHashMap();
        map.put("userName", "eeeeeeeeeee");
        map.put("userId", 13);
        Map<String, Object> map2 = new LinkedHashMap();
        map2.put("userName", "ffffffffff");
        map2.put("userId", 14);
        daoFactory.defaultDao.executeBatchByArrayMaps(updateSQL, map, map2);
        String insertSQL = "insert t_system_user(user_name) values(?)";
        daoFactory.defaultDao.executeArray(insertSQL, "u03271549");
    }
}
