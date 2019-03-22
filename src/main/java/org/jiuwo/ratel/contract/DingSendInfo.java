package org.jiuwo.ratel.contract;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Steven Han
 */
@Getter
@Setter
public class DingSendInfo {
    /**
     * 接收人
     */
    private String sendWho;

    /**
     * 类型
     */
    private String type;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 提醒内容
     */
    private String remindDesc;
}
