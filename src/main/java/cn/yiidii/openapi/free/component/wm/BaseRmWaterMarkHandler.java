package cn.yiidii.openapi.free.component.wm;

import java.util.List;

/**
 * 去水印 基础类
 *
 * @author YiiDii Wang
 * @create 2021-08-04 17:00
 */
public interface BaseRmWaterMarkHandler<T> {

    /**
     * 去水印
     *
     * @param linkList 视频链接
     * @return 去水印VO
     */
    List<T> remove(List<String> linkList);

}
