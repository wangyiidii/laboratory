package cn.yiidii.openapi.component.wm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yiidii.openapi.model.vo.RmWaterMarkVO;
import cn.yiidii.openapi.model.vo.RmWaterMarkVO.Item;
import cn.yiidii.openapi.model.vo.RmWaterMarkVO.Item.ItemBuilder;
import cn.yiidii.openapi.model.vo.RmWaterMarkVO.RmWaterMarkVOBuilder;
import cn.yiidii.pigeon.common.core.util.SpringContextHolder;
import cn.yiidii.pigeon.common.strategy.annotation.HandlerType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 抖音去水印实现
 *
 * @author YiiDii Wang
 * @create 2021-08-04 17:05
 */
@HandlerType(bizCode = WaterMarkConstant.DOUYIN_RM_WATER_MARK, beanName = WaterMarkConstant.DOUYIN_RM_WATER_MARK)
@Component(WaterMarkConstant.DOUYIN_RM_WATER_MARK)
public class DouYinHandler implements BaseRmWaterMarkHandler<RmWaterMarkVO> {

    private static final String DY_VIDEO_PATH = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";

    /**
     * 去水印
     *
     * @param linkList 视频链接
     * @return 去水印VO
     */
    @Override
    public List<RmWaterMarkVO> remove(List<String> linkList) {
        List<String> itemIdList = getItemIdFromShortLink(linkList);
        return getVideo(itemIdList);
    }

    /**
     * 获取itemId
     *
     * @param shortLinkList 抖音短链接
     * @return itemId
     */
    private List<String> getItemIdFromShortLink(List<String> shortLinkList) {
        ThreadPoolTaskExecutor asyncExecutor = SpringContextHolder.getBean("asyncExecutor", ThreadPoolTaskExecutor.class);
        // 最终的itemIdList
        List<String> itemIdList = Lists.newArrayList();
        // 异步执行任务
        CompletableFuture[] completableFutures = shortLinkList.stream().map(l -> CompletableFuture.supplyAsync(() -> {
            // 这里获取itemId
            HttpResponse resp = HttpRequest.get(l)
                    .header(HttpHeaders.USER_AGENT, WaterMarkConstant.MOBILE_UA)
                    .execute();
            String url = Jsoup.parse(resp.body()).getElementsByTag("a").attr("href");
            return url.substring(url.indexOf("video/"), url.lastIndexOf("/")).replace("video/", "");
        }, asyncExecutor).whenComplete((s, throwable) -> {
            // 放入itemIdList
            if (Objects.isNull(throwable)) {
                itemIdList.add(s);
            }
        })).toArray(CompletableFuture[]::new);
        // 阻塞等待所有线程执行完成
        CompletableFuture.allOf(completableFutures).join();
        return itemIdList;
    }

    /**
     * 获取视频地址
     *
     * @param itemIdList 获取itemId
     * @return 去水印VO
     */
    private static List<RmWaterMarkVO> getVideo(List<String> itemIdList) {
        String idStr = CollUtil.join(itemIdList, ",");
        HttpResponse resp = HttpRequest.get(DY_VIDEO_PATH + idStr)
                .header(HttpHeaders.USER_AGENT, WaterMarkConstant.MOBILE_UA)
                .execute();
        Map<String, List<Object>> authorMap = JSONObject.parseObject(resp.body()).getJSONArray("item_list").stream().collect(Collectors.groupingBy(e -> {
            JSONObject jo = (JSONObject) e;
            return jo.getJSONObject("author").getString("short_id");
        }));

        JSONArray itemList = JSONObject.parseObject(resp.body()).getJSONArray("item_list");
        return itemList.stream().map(e -> {
            JSONObject info = (JSONObject) e;
            RmWaterMarkVOBuilder builder = RmWaterMarkVO.builder();
            builder.randomId(RandomUtil.randomString(12));
            // 作者基本信息
            JSONObject author = info.getJSONObject("author");
            String shortId = info.getJSONObject("author").getString("short_id");
            builder.shortId(shortId);
            builder.author(author.getString("nickname"));
            builder.avatar(author.getJSONObject("avatar_medium").getJSONArray("url_list").getString(0));
            builder.signature(author.getString("signature"));
            // 根据image大小判断是图片还是视频
            JSONArray imageJa = info.getJSONArray("images");
            boolean isVideo = Objects.isNull(imageJa) || imageJa.isEmpty();
            ItemBuilder itemBuilder = Item.builder();
            itemBuilder.desc(info.getString("desc"));

            List<String> urlList;
            if (isVideo) {
                JSONArray urlJa = info.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list");
                JSONArray poster = info.getJSONObject("video").getJSONObject("dynamic_cover").getJSONArray("url_list");
                urlList = JSONArray.parseArray(JSON.toJSONString(urlJa), String.class);
                urlList = urlList.stream().map(url -> url.replace("playwm", "play")).collect(Collectors.toList());
                itemBuilder.poster(JSONArray.parseArray(JSON.toJSONString(poster), String.class));
            } else {
                urlList = info.getJSONArray("images").stream().map(imgObj -> {
                    JSONObject imgJo = (JSONObject) imgObj;
                    return imgJo.getJSONArray("url_list").getString(0);
                }).collect(Collectors.toList());
            }
            Item item = itemBuilder.urlList(urlList).build();
            if (isVideo) {
                builder.video(item);
            } else {
                builder.image(item);
            }
            return builder.build();
        }).collect(Collectors.toList());
    }
}
