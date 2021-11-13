package cn.yiidii.openapi.free.model.vo.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AdminNotifyVO
 *
 * @author YiiDii Wang
 * @create 2021-11-13 14:20
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AdminNotifyVO {

    private String title = "系统通知";
    private String content;

}
