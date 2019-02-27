package com.hsh.net.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网络测试任务对象
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/25 15:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PingTaskBean {
    
    private String ip;
    private int total;
    private int size;
    private boolean sendMail;
    private int minTimeOutCount;
    private PingResultBean resultBean;
    
    /**
     * 判断是否需要进行通知
     *
     * @return true-需要，false-不需要
     */
    public boolean isNotice() {
        return sendMail && resultBean.getTimeOutCount() >= minTimeOutCount;
    }
}
