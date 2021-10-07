package cn.yiidii.openapi.free.model.form.jd;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 * chrome jd 登录表单
 *
 * @author YiiDii Wang
 * @create 2021-09-25 11:40
 */
@Data
public class JdChromeLoginForm {

    @NotNull(message = "验证码不能为空")
    @Pattern(regexp = "\\d{4,6}", message = "验证码格式不正确")
    private String code;

    private boolean mock;

}
