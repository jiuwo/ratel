package org.jiuwo.ratel.contract;

import com.aventstack.extentreports.Status;
import lombok.Data;

/**
 * @author Steven Han
 */
@Data
public class ReportResult {
    /**
     * 测试状态
     */
    private Status status;
    /**
     * 接口名称
     */
    private String apiName;

    public ReportResult(String apiName, Status status) {
        this.apiName = apiName;
        this.status = status;
    }
}
