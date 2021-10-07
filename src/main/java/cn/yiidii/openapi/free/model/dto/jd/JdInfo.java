package cn.yiidii.openapi.free.model.dto.jd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 京东相关信息
 *
 * @author YiiDii Wang
 * @create 2021-06-01 10:16
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class JdInfo {

    private String sToken;
    private String guid;
    private String lsId;
    private String lsToken;
    private String preCookie;
    private String oklToken;
    private String token;
    private String qrCodeUrl;
    private String qrCodeBase64;
    private String cookie;
    private String ua;

}
