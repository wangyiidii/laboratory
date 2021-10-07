package cn.yiidii.openapi.free.model.entity.system;

import cn.yiidii.pigeon.common.core.base.entity.SuperEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 访问记录
 *
 * @author YiiDii Wang
 * @create 2021-07-31 23:28
 */
@Data
@Accessors(chain = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("access_record")
public class AccessRecord extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;

    private String path;

    private String queryString;

    private String ip;

    private String loc;

    private String os;

    private String platform;

    private String browser;

    private String version;

    private String deviceName;

    private String deviceModel;

    private String deviceVersion;

    private String engine;

    private String engineVersion;

    private String uaStr;
}
