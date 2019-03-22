package org.jiuwo.ratel.testng;

import org.jiuwo.ratel.contract.ApiDetail;
import org.jiuwo.ratel.contract.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Steven Han
 */
@Component
public class TestDataLoader {

    /**
     * 测试数据
     */
    @Autowired
    private TestData testData;

    public Object[][] getListAndToArray() {

        Object[][] objArr = new Object[testData.getTestDataList().size()][1];
        int i = 0;
        for (ApiDetail apiDetail : testData.getTestDataList()) {
            objArr[i] = new Object[]{apiDetail};
            i++;
        }
        return objArr;
    }

}
