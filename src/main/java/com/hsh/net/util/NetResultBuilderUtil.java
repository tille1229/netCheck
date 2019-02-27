package com.hsh.net.util;

import com.hsh.net.bean.PingDetailBean;
import com.hsh.net.bean.PingResultBean;

/**
 * 网络操作结果构造工具类
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/22 17:28
 */
public final class NetResultBuilderUtil {
    
    private final static String detailOKFlag = "来自";
    private final static String detailTimeOutFlag = "请求超时";
    private final static String statResultFlag = "最短";
    
    private NetResultBuilderUtil() {
    }
    
    /**
     * 生成ping详情
     *
     * @param line ping结果数据
     * @return ping详情
     */
    public static PingDetailBean bulidDetail(String line) {
        String s = line.replaceAll("来自 ", "").replaceAll(" 的回复: 字节=", "&").replaceAll(" 时间<", "&").replaceAll(" 时间=", "&").replaceAll("ms TTL=", "&");
        String[] resultArr = s.split("&");
        return new PingDetailBean(resultArr[0], Integer.parseInt(resultArr[1]), Integer.parseInt(resultArr[2]), Integer.parseInt(resultArr[3]));
    }
    
    /**
     * 生成ping结果信息
     *
     * @param line ping结果数据
     * @return ping详情
     */
    public static PingResultBean bulidResult(String ip, int totalCount, int byteSize) {
        PingResultBean resultBean = new PingResultBean();
        resultBean.setIp(ip);
        resultBean.setTotalCount(totalCount);
        resultBean.setByteSize(byteSize);
        return resultBean;
    }
    
    public static PingResultBean bulidStatResult(PingResultBean resultBean, String line) {
        String s = line.replaceAll("    最短 = ", "&").replaceAll("，最长 = ", "&").replaceAll("，平均 = ", "&").replaceAll("ms", "");
        String[] resultArr = s.split("&");
        resultBean.setMinTime(Integer.parseInt(resultArr[1]));
        resultBean.setMaxTime(Integer.parseInt(resultArr[2]));
        resultBean.setAvgTime(Integer.parseInt(resultArr[3]));
        return resultBean;
    }
    
    /**
     * 校验数据是否是正常的ping结果
     *
     * @param line ping结果数据
     * @return ture-是，false-不是
     */
    public static boolean checkDetialOK(String line) {
        return line.indexOf(detailOKFlag) > -1;
    }
    
    /**
     * 校验数据是否是超时的ping结果
     *
     * @param line ping结果数据
     * @return ture-是，false-不是
     */
    public static boolean checkDetialTimeOut(String line) {
        return line.indexOf(detailTimeOutFlag) > -1;
    }
    
    /**
     * 校验数据是否包含ping统计信息
     *
     * @param line ping结果数据
     * @return ture-是，false-不是
     */
    public static boolean checkStatResult(String line) {
        return line.indexOf(statResultFlag) > -1;
    }
}
