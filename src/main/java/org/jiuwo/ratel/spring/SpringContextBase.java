package org.jiuwo.ratel.spring;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * @author Steven Han
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class SpringContextBase extends AbstractTestNGSpringContextTests {

}
