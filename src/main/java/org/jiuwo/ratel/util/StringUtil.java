package org.jiuwo.ratel.util;


import org.jiuwo.ratel.constant.SysConstant;

/**
 * @author Steven Han
 */
public class StringUtil {

    public static String getSubject(String subject) {
        return String.format("[%s] %s", SysConstant.SYS_NAME, subject);
    }
}
