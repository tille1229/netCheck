package com.hsh.net.bean;

import cn.hutool.core.text.StrFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.text.DecimalFormat;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ping结果对象
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/22 17:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PingResultBean {
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 平均延时
     */
    private int avgTime;
    
    /**
     * 最小延时
     */
    private int minTime;
    
    /**
     * 最大延时
     */
    private int maxTime;
    
    /**
     * 总次数
     */
    private int totalCount;
    
    /**
     * 单包大小
     */
    private int byteSize;
    
    /**
     * 正常次数
     */
    private int normalCount;
    
    /**
     * 超时次数
     */
    private int timeOutCount;
    
    /**
     * 正常数据详情
     */
    @JsonIgnore
    private List<PingDetailBean> detailBeanList;
    
    public String buildMailContent() {
        //1.定义模板
        StringBuffer content = new StringBuffer("本次测试服务器为：<font style='color:green'>{}</font>，共计ping{}次，超时{}次，详细测试结果如下：<br/>");
        content.append("<span style='width:40px'></span>网络波动：<font style='color:green'>{}</font>%⬆/<font style='color:green'>{}</font>%⬇<br/>");
        content.append("<span style='width:40px'></span>成功率：<font style='color:green'>{}</font>%<br/>");
        content.append("<span style='width:40px'></span>最大延时(ms)：<font style='color:green'>{}</font><br/>");
        content.append("<span style='width:40px'></span>最小延迟(ms)：<font style='color:green'>{}</font><br/>");
        content.append("<span style='width:40px'></span>平均延时(ms)：<font style='color:green'>{}</font><br/>");
        
        //2.计算成功率
        DecimalFormat fmt = new DecimalFormat("##0");
        String successRate = fmt.format(normalCount * 100 / totalCount);
        
        //3.计算波动幅度
        String minFlu = fmt.format((avgTime - minTime) * 100 / avgTime);
        String maxFlu = fmt.format((maxTime - avgTime) * 100 / avgTime);
        return StrFormatter.format(content.toString(), ip, totalCount, timeOutCount, maxFlu, minFlu, successRate, maxTime, minTime, avgTime);
    }
}
