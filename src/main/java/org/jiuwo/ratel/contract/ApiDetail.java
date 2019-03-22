package org.jiuwo.ratel.contract;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Steven Han
 */
@Getter
@Setter
public class ApiDetail {
    /**
     * 接口名称
     */
    private String apiName;
    /**
     * 接口地址
     */
    private String apiUrl;
    /**
     * 参数
     */
    private String apiParams;
    /**
     * Header
     */
    private String apiHeader;
    /**
     * 判断条件：returncode==0
     */
    private String condition;
    /**
     * 方式 0GET  1POST
     */
    private int method;

    /**
     * 是否签名 1是 0否
     */
    private int isSign;

    private String appKey;

    /**
     * 用于报告显示名称
     *
     * @return apiName
     */
    @Override
    public String toString() {
        return this.apiName;
    }
}