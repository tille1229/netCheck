package com.hsh.net.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ping结果对象
 *
 * @author hsh
 * @version V1.0
 * @date 2019/2/22 17:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PingDetailBean {
    
    private String hostName;
    private int pingSize;
    private int time;
    private int ttl;
}
