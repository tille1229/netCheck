package com.hsh.net.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.hsh.net.bean.PingResultBean;
import com.hsh.net.bean.PingTaskBean;
import com.hsh.net.cache.PingResultCache;
import com.hsh.net.util.PingUtil;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 网络测试定时任务
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/25 15:14
 */

@Getter
@Setter
@Configuration
@EnableScheduling
@Slf4j(topic = "【网络测试定时任务】")
public class PingTask {
    
    /**
     * 配置文件地址
     */
    private static final String CONFIG_FILE_PATH = "config/ipTask.setting";
    
    /**
     * 线程调度池
     */
    private static final ExecutorService EXECUTOR_SERVICE = ThreadUtil.newExecutor();
    
    /**
     * 任务集合list
     */
    private List<PingTaskBean> taskBeanList;
    
    /**
     * 通知邮件地址
     */
    private String noticeMail;
    
    /**
     * 执行操作
     */
    public void doWork() {
        String now = DateUtil.now();
        for (PingTaskBean pingTaskBean : getTaskBeanList()) {
            EXECUTOR_SERVICE.execute(() -> {
                    doWorkDetail(now, pingTaskBean);
                }
            );
        }
    }
    
    /**
     * 解析配置文件
     */
    public void parseConfigFile() {
        //1.读取配置文件
        Setting setting = new Setting(CONFIG_FILE_PATH);
        
        //2.加载配置文件
        List<PingTaskBean> list = Arrays.stream(setting.get("ip", "addr").split(",")).map(ip -> {
            String total = setting.get(ip, "total");
            String size = setting.get(ip, "size");
            String sendMail = setting.get(ip, "sendMail");
            String minTimeOutCount = setting.get(ip, "minTimeOutCount");
            return new PingTaskBean(ip, Integer.parseInt(total), Integer.parseInt(size), Boolean.parseBoolean(sendMail), Integer.parseInt(minTimeOutCount), null);
        }).collect(Collectors.toList());
        
        //3.设置配置参数
        setTaskBeanList(list);
        setNoticeMail(setting.get("ip", "noticeMailAccount"));
        
    }
    
    /**
     * 执行定时任务,每十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    private void doTask() {
        doWork();
    }
    
    /**
     * 执行任务
     *
     * @param now 当前时间点
     * @param pingTaskBean 执行任务的详情
     */
    private void doWorkDetail(String now, PingTaskBean pingTaskBean) {
        PingResultBean pingResultBean = PingUtil.ping(pingTaskBean.getIp(), pingTaskBean.getTotal(), pingTaskBean.getSize());
        pingTaskBean.setResultBean(pingResultBean);
        PingResultCache.put(pingResultBean.getIp(), pingResultBean);
        if (pingTaskBean.isNotice()) {
            log.info("监测IP为{},推送地址为{},监测到本次的结果需要进行邮件推送，开始进行推送...", pingTaskBean.getIp(), noticeMail);
            String subject = StrFormatter.format("{}-网络检查结果（{}）", pingTaskBean.getIp(), now);
            MailUtil.send(noticeMail, subject, pingResultBean.buildMailContent(), true);
            log.info("监测IP为{},推送地址为{},邮件推送结束.", pingTaskBean.getIp(), noticeMail);
        } else {
            pingResultBean.setDetailBeanList(null);
            log.info("监测IP为{},本次检查结果为:{}", pingTaskBean.getIp(), JSONUtil.toJsonStr(pingResultBean));
        }
    }
    
    @PostConstruct
    private void init() {
        parseConfigFile();
        doWork();
    }
    
}
