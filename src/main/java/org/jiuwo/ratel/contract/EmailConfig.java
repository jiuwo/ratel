package org.jiuwo.ratel.contract;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tiamwei
 */
@Setter
@Getter
public class EmailConfig {
    /**
     * SMTP
     */
    private String host;
    /**
     * PORT
     */
    private int port;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

}
