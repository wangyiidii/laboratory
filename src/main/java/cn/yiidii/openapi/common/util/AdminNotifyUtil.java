package cn.yiidii.openapi.common.util;

import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.model.vo.mail.AdminNotifyVO;
import cn.yiidii.pigeon.common.mail.core.MailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * AdminNotifyUtil
 *
 * @author YiiDii Wang
 * @create 2021-11-13 21:44
 */
@Component
@RequiredArgsConstructor
public class AdminNotifyUtil {

    private static final String ADMIN_NOTIFY_TPL = "adminNotify.ftl";

    private final Environment environment;
    private final MailTemplate mailTemplate;

    /**
     * 通知
     *
     * @param subject 邮件subject
     * @param vo      vo
     */
    public void doNotify(String subject, AdminNotifyVO vo) {
        String adminEmail = environment.getProperty("pigeon.lab.adminNotify.mail");
        Boolean enabled = Boolean.valueOf(environment.getProperty("pigeon.lab.adminNotify.enabled"));
        if (StrUtil.isBlank(adminEmail) || !enabled) {
            return;
        }
        mailTemplate.sendTemplateHtmlMail(subject, "", new String[]{adminEmail}, ADMIN_NOTIFY_TPL, vo);
    }
}
