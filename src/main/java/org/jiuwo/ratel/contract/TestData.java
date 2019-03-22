package org.jiuwo.ratel.contract;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Steven Han
 */
@Getter
@Setter
public class TestData {
    /**
     * 需要测试的接口列表
     */
    private List<ApiDetail> testDataList;

}