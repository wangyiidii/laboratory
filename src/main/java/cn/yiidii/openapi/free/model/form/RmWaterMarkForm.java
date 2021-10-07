package cn.yiidii.openapi.free.model.form;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 去水印表单
 *
 * @author YiiDii Wang
 * @create 2021-08-04 16:53
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class RmWaterMarkForm {

    @NotNull(message = "bizCode不能为空")
    private String bizCode;
    private List<String> links;
}
