package org.jiuwo.ratel.util;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.jiuwo.ratel.contract.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Steven Han
 */
@Slf4j
@Component
public class MailUtil {

    private static MailUtil mailUtil;

    @Autowired
    private EmailConfig emailConfig;

    @PostConstruct
    public void init() {
        mailUtil = this;
    }

    public static boolean sendHtmlEmail(String to, String subject, String htmlContent, String[] attachFileNames) {
        if (!verifyEmailConfig()) {
            return false;
        }
        Session session = getSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(mailUtil.emailConfig.getUsername()));
            message.addRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(StringUtil.getSubject(subject), "UTF-8");
            Multipart multipart = new MimeMultipart();
            BodyPart partContent = new MimeBodyPart();
            partContent.setContent(htmlContent, "text/html;charset=utf-8");
            multipart.addBodyPart(partContent);

            int length = attachFileNames == null ? 0 : attachFileNames.length;
            for (int i = 0; i < length; i++) {
                if (StringUtils.isEmpty(attachFileNames[i])) {
                    continue;
                }
                BodyPart part = new MimeBodyPart();
                // 根据文件名获取数据源
                DataSource dataSource = new FileDataSource(attachFileNames[i]);
                DataHandler dataHandler = new DataHandler(dataSource);
                // 得到附件本身并至入BodyPart
                part.setDataHandler(dataHandler);
                // 得到文件名同样至入BodyPart
                part.setFileName(MimeUtility.encodeText(dataSource.getName()));
                multipart.addBodyPart(part);
            }
            message.setContent(multipart);
            Transport.send(message);
            return true;
        } catch (Exception ex) {
            log.error("error when send HtmlEmail.", ex);
            return false;
        }
    }

    public static boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        return sendHtmlEmail(to, subject, htmlContent, new String[]{});
    }

    public static boolean sendHtmlEmail(String to, String subject, String htmlContent, String attachFileName) {
        return sendHtmlEmail(to, subject, htmlContent, new String[]{attachFileName});
    }

    public static boolean sendEmail(String to, String subject, String content) {
        if (!verifyEmailConfig()) {
            return false;
        }
        Session session = getSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(mailUtil.emailConfig.getUsername()));
            message.addRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(StringUtil.getSubject(subject), "UTF-8");
            message.setText(content);
            Transport.send(message);
            return true;
        } catch (MessagingException ex) {
            log.error("error when send email.", ex);
            return false;
        }
    }

    private static boolean verifyEmailConfig() {
        if (mailUtil.emailConfig == null) {
            log.warn("没有配置邮件系统");
            return false;
        }
        return true;
    }

    private static Session getSession() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", mailUtil.emailConfig.getHost());
        properties.put("mail.smtp.port", mailUtil.emailConfig.getPort());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.transport.protocol", "smtp");


        Session session = Session.getDefaultInstance(properties, new SimpleAuthenticator(mailUtil.emailConfig.getUsername(), mailUtil.emailConfig.getPassword()));
        return session;
    }

    static class SimpleAuthenticator extends Authenticator {

        private String username;

        private String password;

        public SimpleAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.username, this.password);

        }

    }

}

