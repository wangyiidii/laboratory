package cn.yiidii.openapi.free.common.interceptor;

import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.util.WebUtils;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 弃用api拦截器
 *
 * @author YiiDii Wang
 * @create 2021-09-03 10:48
 */
@Component
public class DeprecatedApiInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod h = (HandlerMethod) handler;
            Deprecated deprecatedAnno = h.getMethodAnnotation(Deprecated.class);
            if (Objects.nonNull(deprecatedAnno)) {
                WebUtils.renderJson(response, R.failed("接口已弃用"));
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }
}
