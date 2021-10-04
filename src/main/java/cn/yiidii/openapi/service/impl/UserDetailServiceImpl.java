package cn.yiidii.openapi.service.impl;

import cn.yiidii.pigeon.common.security.service.PigeonUser;
import cn.yiidii.pigeon.common.security.service.PigeonUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 用户详情服务
 *
 * @author YiiDii Wang
 * @create 2021-10-01 23:32
 */
@Component
@Slf4j
public class UserDetailServiceImpl implements PigeonUserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: {}", username);
        String[] permissionArray = new String[]{"/jd/check"};
        return new PigeonUser(1L,
                "admin",
                "admin",
                true,
                true,
                true,
                true,
                AuthorityUtils.createAuthorityList(permissionArray));
    }

}
