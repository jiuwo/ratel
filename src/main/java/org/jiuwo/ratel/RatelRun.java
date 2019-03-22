package org.jiuwo.ratel;

import org.apache.commons.lang3.StringUtils;
import org.jiuwo.fastel.Expression;
import org.jiuwo.fastel.impl.ExpressionImpl;
import org.jiuwo.ratel.contract.ApiDetail;
import org.jiuwo.ratel.contract.TestData;
import org.jiuwo.ratel.contract.enums.EnableEnum;
import org.jiuwo.ratel.contract.enums.MethodEnum;
import org.jiuwo.ratel.exception.RatelException;
import org.jiuwo.ratel.spring.SpringContextBase;
import org.jiuwo.ratel.testng.TestDataLoader;
import org.jiuwo.ratel.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 自动化测试主类
 *
 * @author Steven Han
 */
public class RatelRun extends SpringContextBase {

    private Expression expression = new ExpressionImpl();

    @Autowired
    TestData testData;

    @Autowired
    TestDataLoader testDataLoader;


    @BeforeClass()
    public void beforeClass() {
    }

    @DataProvider(name = "loadTestData")
    public Object[][] loadTestData() {
        return testDataLoader.getListAndToArray();
    }

    @Test(groups = {"defaultGroup"}, dataProvider = "loadTestData")
    public void run(ApiDetail apiDetail) {
        verifyParams(apiDetail);
        Reporter.log("地址：" + apiDetail.getApiUrl());
        Reporter.log("条件：" + apiDetail.getCondition());

        if (StringUtils.isNotEmpty(apiDetail.getApiParams())) {
            Reporter.log("参数：" + apiDetail.getApiParams());
        }

        if (StringUtils.isNotEmpty(apiDetail.getApiHeader())) {
            Reporter.log("header：" + apiDetail.getApiHeader());
        }

        String result;
        if (apiDetail.getMethod() == MethodEnum.GET.getMethod()) {
            result = runGet(apiDetail);
        } else {
            result = runPost(apiDetail);
        }

        Reporter.log("结果：" + result);


        JSONObject obj = JSON.parseObject(result);
        expression = new ExpressionImpl(apiDetail.getCondition());
        Assert.assertEquals(true, expression.evaluate(obj));

    }

    private void verifyParams(ApiDetail apiDetail) {
        if (StringUtils.isEmpty(apiDetail.getApiName())) {
            throw new RatelException("apiName不能为空", apiDetail);
        }
        if (StringUtils.isEmpty(apiDetail.getApiUrl())) {
            throw new RatelException("apiUrl不能为空", apiDetail);
        }
        if (apiDetail.getIsSign() == EnableEnum.ENABLE.getKey()) {
            if (StringUtils.isEmpty(apiDetail.getAppKey())) {
                throw new RatelException("开启需要签名时加密Key不能为空", apiDetail);
            }
        }
    }

    private String runGet(ApiDetail apiDetail) {
        long start = System.currentTimeMillis();
        String result = HttpUtil.get(apiDetail.getApiUrl());
        long end = System.currentTimeMillis();
        Reporter.log(String.format("耗时：%d ms", (end - start)));
        return result;
    }

    private String runPost(ApiDetail apiDetail) {
        long start = System.currentTimeMillis();
        String result = HttpUtil.postJson(apiDetail.getApiUrl(), apiDetail.getApiParams());
        long end = System.currentTimeMillis();
        Reporter.log(String.format("耗时：%d ms", (end - start)));
        return result;
    }

}
