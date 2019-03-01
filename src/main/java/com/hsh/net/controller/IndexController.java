package com.hsh.net.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.hsh.net.bean.PingResultBean;
import com.hsh.net.bean.PingTaskBean;
import com.hsh.net.cache.PingResultCache;
import com.hsh.net.task.PingTask;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/27 14:38
 */
@RestController
public class IndexController {
    
    @Autowired
    PingTask task;
    
    @GetMapping("/result/{ip}")
    public PingResultBean findByIp(@PathVariable(name = "ip") String ip) {
        return PingResultCache.get(ip);
    }
    
    @PostMapping("/login/login")
    public ResponseEntity<Map<String, Object>> login() {
        Map<String, Object> resultMap = MapUtil.newHashMap();
        resultMap.put("roles", CollectionUtil.toList("admin"));
        resultMap.put("token", "admin");
        resultMap.put("introduction", "我是超级管理员");
        resultMap.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        resultMap.put("name", "Super Admin");
        return ResponseEntity.ok(resultMap);
    }
    
    @GetMapping("/tasks")
    public List<PingTaskBean> taskList() {
        List<PingTaskBean> taskBeanList = task.getTaskBeanList();
        return taskBeanList;
    }
}
