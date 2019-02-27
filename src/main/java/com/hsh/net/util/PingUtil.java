package com.hsh.net.util;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import com.hsh.net.bean.PingDetailBean;
import com.hsh.net.bean.PingResultBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 网络测试工具类
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/22 17:30
 */
@Slf4j(topic = "【网络测试工具类】")
public class PingUtil {
    
    private PingUtil() {
    }
    
    /**
     * 进行PING测试
     *
     * @param ip ip地址
     * @param count 测试次数
     * @param size 包大小
     * @return 测试结果对象
     */
    public static PingResultBean ping(String ip, int count, int size) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        String line = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        Boolean res = false;
        PingResultBean resultBean = NetResultBuilderUtil.bulidResult(ip, count, size);
        List<PingDetailBean> detailBeanList = new ArrayList<>();
        int timeoutCount = 0;
        try {
            String exeStr = StrFormatter.format("ping {}  -n {} -l {}", ip, count, size);
            process = runtime.exec(exeStr);
            is = process.getInputStream();
            isr = new InputStreamReader(is, Charset.forName("GBK"));
            br = new BufferedReader(isr);
            
            while ((line = br.readLine()) != null) {
                if (NetResultBuilderUtil.checkDetialOK(line)) {
                    detailBeanList.add(NetResultBuilderUtil.bulidDetail(line));
                }
                if (NetResultBuilderUtil.checkDetialTimeOut(line)) {
                    timeoutCount++;
                }
                if (NetResultBuilderUtil.checkStatResult(line)) {
                    NetResultBuilderUtil.bulidStatResult(resultBean, line);
                }
            }
            resultBean.setTimeOutCount(timeoutCount);
            resultBean.setNormalCount(count - timeoutCount);
            resultBean.setDetailBeanList(detailBeanList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
            
            
        }
        return resultBean;
    }
    
}
