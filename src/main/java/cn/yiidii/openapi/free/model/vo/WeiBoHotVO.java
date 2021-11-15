package cn.yiidii.openapi.free.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WeiBoHotVO
 *
 * @author YiiDii Wang
 * @create 2021-11-15 10:06
 */
@Data
@Accessors(chain = true)
public class WeiBoHotVO {

    private Integer rank;
    private String keyword;
    private String href;
    private Long hotValue;
    private String tag;
}