package com.hsh.net.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.hsh.net.bean.PingResultBean;
import com.hsh.net.bean.PingTaskBean;
import com.hsh.net.util.PingUtil;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import sun.applet.Main;

/**
 * 网络测试定时任务
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/25 15:14
 */
@EnableScheduling
@Slf4j(topic = "【网络测试定时任务】")
@Configuration
public class PingTask {
    
    private final static ExecutorService executorService = ThreadUtil.newExecutor();
    private static List<PingTaskBean> taskBeanList = null;
    private static String noticeMail = "";
    
    static {
        log.info("开始解析网络配置文件...");
        Setting setting = new Setting("config/ipTask.setting");
        String addr = setting.get("ip", "addr");
        noticeMail = setting.get("ip", "noticeMailAccount");
        String[] split = addr.split(",");
        taskBeanList = Arrays.stream(split).map(ip -> {
            String total = setting.get(ip, "total");
            String size = setting.get(ip, "size");
            String sendMail = setting.get(ip, "sendMail");
            String minTimeOutCount = setting.get(ip, "minTimeOutCount");
            return new PingTaskBean(ip, Integer.parseInt(total), Integer.parseInt(size), Boolean.parseBoolean(sendMail), Integer.parseInt(minTimeOutCount), null);
        }).collect(Collectors.toList());
        log.info("网络配置文件解析完成。");
        
    }
    
    
    /**
     * 执行操作
     */
    public static void doWork() {
        String now = DateUtil.now();
        for (PingTaskBean pingTaskBean : taskBeanList) {
            executorService.execute(() -> {
                    PingResultBean pingResultBean = PingUtil.ping(pingTaskBean.getIp(), pingTaskBean.getTotal(), pingTaskBean.getSize());
                    pingTaskBean.setResultBean(pingResultBean);
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
            );
        }
    }
    
    /**
     * 执行定时任务,每十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    @PostConstruct
    public void doTask() {
        doWork();
    }
    
}
