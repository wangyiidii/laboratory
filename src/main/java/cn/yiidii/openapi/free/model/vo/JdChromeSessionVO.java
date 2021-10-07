package cn.yiidii.openapi.free.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * chrome jd session vo
 *
 * @author YiiDii Wang
 * @create 2021-09-23 13:07
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JdChromeSessionVO {

    private String sessionId;
    private LocalDateTime createTime;
    private String ck;
}
