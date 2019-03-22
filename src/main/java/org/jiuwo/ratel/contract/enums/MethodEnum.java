package org.jiuwo.ratel.contract.enums;

import lombok.Getter;

/**
 * @author Steven Han
 */
@Getter
public enum MethodEnum {

    /**
     * GET 请求
     */
    GET(0, "GET"),
    /**
     * POST 请求
     */
    POST(1, "POST");

    private int method;

    private String methodName;

    MethodEnum(int method, String methodName) {
        this.method = method;
        this.methodName = methodName;
    }

}
