package com.hsh.net.controller;

import com.hsh.net.bean.PingResultBean;
import com.hsh.net.bean.PingTaskBean;
import com.hsh.net.cache.PingResultCache;
import com.hsh.net.task.PingTask;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @GetMapping("/tasks")
    public List<PingTaskBean> taskList() {
        List<PingTaskBean> taskBeanList = task.getTaskBeanList();
        return taskBeanList;
    }
}
