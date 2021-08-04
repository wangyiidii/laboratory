package cn.yiidii.openapi.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yiidii.openapi.model.vo.DouYinVideoVO;
import cn.yiidii.openapi.model.vo.DouYinVideoVO.DouYinVideoVOBuilder;
import cn.yiidii.openapi.model.vo.DouYinVideoVO.Video;
import cn.yiidii.openapi.model.vo.DouYinVideoVO.Video.VideoBuilder;
import cn.yiidii.pigeon.common.core.util.SpringContextHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 去除水印工具
 *
 * @author YiiDii Wang
 * @create 2021-08-04 11:50
 */
@UtilityClass
public class WaterMarkUtil {

    private static final String MOBILE_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";
    private static final String DY_VIDEO_PATH = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";

    /**
     * 抖音去水印
     *
     * @param shortLinkList 抖音短链接
     * @return DouYinVideoVO
     */
    public List<DouYinVideoVO> rmDouYinWaterMark(List<String> shortLinkList) {
        List<String> itemIdList = getItemIdFromShortLink(shortLinkList);
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
                    .header(HttpHeaders.USER_AGENT, MOBILE_UA)
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
     * @return DouYinVideoVO
     */
    private static List<DouYinVideoVO> getVideo(List<String> itemIdList) {
        String idStr = CollUtil.join(itemIdList, ",");
        HttpResponse resp = HttpRequest.get(DY_VIDEO_PATH + idStr)
                .header(HttpHeaders.USER_AGENT, MOBILE_UA)
                .execute();
        Map<String, List<Object>> authorMap = JSONObject.parseObject(resp.body()).getJSONArray("item_list").stream().collect(Collectors.groupingBy(e -> {
            JSONObject jo = (JSONObject) e;
            return jo.getJSONObject("author").getString("short_id");
        }));
        return authorMap.entrySet().stream().map(e -> {
            DouYinVideoVOBuilder builder = DouYinVideoVO.builder().shortId(e.getKey());
            JSONObject info = (JSONObject) e.getValue().get(0);
            final JSONObject author = info.getJSONObject("author");
            builder.author(author.getString("nickname"));
            builder.avatar(author.getJSONObject("avatar_medium").getJSONArray("url_list").getString(0));
            builder.signature(author.getString("signature"));
            final List<Video> videoList = e.getValue().stream().map(o -> {
                VideoBuilder videoBuilder = Video.builder();
                JSONObject jo = (JSONObject) o;
                videoBuilder.desc(jo.getString("desc"));
                JSONArray urlJa = jo.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list");
                List<String> urlList = JSONArray.parseArray(JSON.toJSONString(urlJa), String.class);
                urlList = urlList.stream().map(url -> url.replace("playwm", "play")).collect(Collectors.toList());
                videoBuilder.videoAddrList(urlList);
                return videoBuilder.build();
            }).collect(Collectors.toList());
            builder.videoList(videoList);
            return builder.build();
        }).collect(Collectors.toList());
    }
}
