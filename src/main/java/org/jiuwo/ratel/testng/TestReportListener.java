package org.jiuwo.ratel.testng;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.jiuwo.ratel.constant.SysConstant;
import org.jiuwo.ratel.contract.ReportResult;
import org.jiuwo.ratel.util.MailUtil;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ResourceCDN;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.TestAttribute;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;

/**
 * @author Steven Han
 */
public class TestReportListener implements IReporter {
    /**
     * 路径
     */
    private static final String OUTPUT_FOLDER = "test-output/";
    /**
     * 文件名
     */
    private static final String FILE_NAME = "%s.html";

    /**
     * 报告标题
     */
    private static final String REPORT_NAME = "%s · 自动化测试报告";

    private ExtentReports extent;

    /**
     * 测试结果
     */
    private List<ReportResult> reportResults = new ArrayList<>();

    private String suiteId;
    private String suiteName;
    private String reportUrl;
    private String reportReceiver;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        init(xmlSuites);
        boolean createSuiteNode = false;
        if (suites.size() > 1) {
            createSuiteNode = true;
        }
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();
            //如果suite里面没有任何用例，直接跳过，不在报告里生成
            if (result.size() == 0) {
                continue;
            }
            //统计suite下的成功、失败、跳过的总用例数
            int suiteFailSize = 0;
            int suitePassSize = 0;
            int suiteSkipSize = 0;
            ExtentTest suiteTest = null;
            //存在多个suite的情况下，在报告中将同一个一个suite的测试结果归为一类，创建一级节点。
            if (createSuiteNode) {
                suiteTest = extent.createTest(suite.getName()).assignCategory(suite.getName());
            }
            boolean createSuiteResultNode = false;
            if (result.size() > 1) {
                createSuiteResultNode = true;
            }
            for (ISuiteResult r : result.values()) {
                ExtentTest resultNode;
                ITestContext context = r.getTestContext();
                if (createSuiteResultNode) {
                    //没有创建suite的情况下，将在SuiteResult的创建为一级节点，否则创建为suite的一个子节点。
                    if (null == suiteTest) {
                        resultNode = extent.createTest(r.getTestContext().getName());
                    } else {
                        resultNode = suiteTest.createNode(r.getTestContext().getName());
                    }
                } else {
                    resultNode = suiteTest;
                }
                if (resultNode != null) {
                    resultNode.getModel().setName(suite.getName() + " : " + r.getTestContext().getName());
                    if (resultNode.getModel().hasCategory()) {
                        resultNode.assignCategory(r.getTestContext().getName());
                    } else {
                        resultNode.assignCategory(suite.getName(), r.getTestContext().getName());
                    }
                    resultNode.getModel().setStartTime(r.getTestContext().getStartDate());
                    resultNode.getModel().setEndTime(r.getTestContext().getEndDate());
                    //统计SuiteResult下的数据
                    int passSize = r.getTestContext().getPassedTests().size();
                    int failSize = r.getTestContext().getFailedTests().size();
                    int skipSize = r.getTestContext().getSkippedTests().size();
                    suitePassSize += passSize;
                    suiteFailSize += failSize;
                    suiteSkipSize += skipSize;
                    if (failSize > 0) {
                        resultNode.getModel().setStatus(Status.FAIL);
                    }
                    resultNode.getModel().setDescription(String.format("Pass: %s ; Fail: %s ; Skip: %s ;", passSize, failSize, skipSize));
                }
                buildTestNodes(resultNode, context.getFailedTests(), Status.FAIL);
                buildTestNodes(resultNode, context.getSkippedTests(), Status.SKIP);
                buildTestNodes(resultNode, context.getPassedTests(), Status.PASS);
            }
            if (suiteTest != null) {
                suiteTest.getModel().setDescription(String.format("Pass: %s ; Fail: %s ; Skip: %s ;", suitePassSize, suiteFailSize, suiteSkipSize));
                if (suiteFailSize > 0) {
                    suiteTest.getModel().setStatus(Status.FAIL);
                }
            }


        }

        //发送邮件
        sendFailMail();

        extent.flush();
    }

    private void init(List<XmlSuite> xmlSuites) {
        setSysConfig(xmlSuites);
        //文件夹不存在的话进行创建
        File reportDir = new File(OUTPUT_FOLDER);
        if (!reportDir.exists() && !reportDir.isDirectory()) {
            reportDir.mkdir();
        }
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(OUTPUT_FOLDER + getFileName(xmlSuites));
        htmlReporter.loadXMLConfig(SysConstant.HTML_CONFIG);
        htmlReporter.config().setDocumentTitle(getReportName());
        htmlReporter.config().setReportName(getReportName());
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);
        htmlReporter.config().setCSS(".node.level-1  ul{ display:none;} .node.level-1.active ul{display:block;}");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setReportUsesManualConfiguration(true);


    }

    private void setSysConfig(List<XmlSuite> xmlSuites) {
        if (xmlSuites == null || xmlSuites.size() == 0) {
            return;
        }
        XmlSuite xmlSuite = xmlSuites.get(0);
        if (xmlSuite == null
                || xmlSuite.getParameters() == null
                || xmlSuite.getParameters().get("suiteId") == null) {
            return;
        }
        this.suiteId = xmlSuite.getParameters().get("suiteId");
        this.suiteName = xmlSuite.getName();
        this.reportUrl = xmlSuite.getParameters().get("reportUrl");
        this.reportReceiver = xmlSuite.getParameters().get("reportReceiver");
    }

    private String getFileName(List<XmlSuite> xmlSuites) {
        if (StringUtils.isEmpty(this.suiteId)) {
            return String.format(FILE_NAME, "");
        }
        return String.format(FILE_NAME, this.suiteId);
    }

    private String getReportName() {
        return String.format(REPORT_NAME, this.suiteName);
    }

    private void buildTestNodes(ExtentTest extenttest, IResultMap tests, Status status) {
        //存在父节点时，获取父节点的标签
        String[] categories = new String[0];
        if (extenttest != null) {
            List<TestAttribute> categoryList = extenttest.getModel().getCategoryContext().getAll();
            categories = new String[categoryList.size()];
            for (int index = 0; index < categoryList.size(); index++) {
                categories[index] = categoryList.get(index).getName();
            }
        }

        ExtentTest test;

        if (tests.size() > 0) {
            //调整用例排序，按时间排序
            Set<ITestResult> treeSet = new TreeSet<ITestResult>(new Comparator<ITestResult>() {
                @Override
                public int compare(ITestResult o1, ITestResult o2) {
                    return o1.getStartMillis() < o2.getStartMillis() ? -1 : 1;
                }
            });
            treeSet.addAll(tests.getAllResults());
            for (ITestResult result : treeSet) {
                Object[] parameters = result.getParameters();
                String name = "";
                //如果有参数，则使用参数的toString组合代替报告中的name
                for (Object param : parameters) {
                    name += param.toString();
                }
                if (name.length() > 0) {
                    if (name.length() > 50) {
                        name = name.substring(0, 49) + "...";
                    }
                } else {
                    name = result.getMethod().getMethodName();
                }
                if (extenttest == null) {
                    test = extent.createTest(name);
                } else {
                    //作为子节点进行创建时，设置同父节点的标签一致，便于报告检索。
                    test = extenttest.createNode(name).assignCategory(categories);
                }

                for (String group : result.getMethod().getGroups()) {
                    test.assignCategory(group);
                }
                List<String> outputList = Reporter.getOutput(result);
                for (String output : outputList) {
                    //将用例的log输出报告中
                    test.debug(output);
                }
                if (result.getThrowable() != null) {
                    test.log(status, result.getThrowable());
                    reportResults.add(new ReportResult(name, status));
                } else {
                    test.log(status, "Test " + status.toString().toLowerCase() + "ed");
                    reportResults.add(new ReportResult(name, status));
                }

                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));
            }
        }
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    /**
     * 发送错误邮件
     */
    private void sendFailMail() {
        if (StringUtils.isEmpty(this.reportReceiver)) {
            //接收者为空时不发送报告
            return;
        }
        int failSize = 0;
        int passSize = 0;
        int skipSize = 0;
        if (reportResults.size() > 0) {
            for (ReportResult reportResult : reportResults) {
                switch (reportResult.getStatus()) {
                    case FAIL:
                        failSize++;
                        break;
                    case PASS:
                        passSize++;
                        break;
                    case SKIP:
                        skipSize++;
                        break;
                    default:
                        break;
                }
            }
        }
        if (failSize > 0) {
            String message = String.format("%s：\n%d PASS  \n%d FAIL \n%d SKIP %s", getReportName(), passSize, failSize, skipSize, getReportUrl());
            MailUtil.sendEmail(this.reportReceiver, getReportName(), message);
        }
    }

    private String getReportUrl() {
        if (StringUtils.isEmpty(this.reportUrl)) {
            return "";
        }
        return String.format("\n\n详情查看：%s", this.reportUrl);
    }
}