package cn.yiidii.openapi.model.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 去水印视频VO
 *
 * @author YiiDii Wang
 * @create 2021-08-04 12:26
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RmWaterMarkVO {

    private String randomId;
    private String shortId;
    private String author;
    private String avatar;
    private String signature;
    private Item video;
    private Item image;

    @Data
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Item {

        private String desc;
        private List<String> poster;
        private List<String> urlList;
    }

}
