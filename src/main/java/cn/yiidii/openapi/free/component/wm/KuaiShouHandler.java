package cn.yiidii.openapi.free.component.wm;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yiidii.openapi.free.model.vo.RmWaterMarkVO;
import cn.yiidii.openapi.free.model.vo.RmWaterMarkVO.Item;
import cn.yiidii.pigeon.common.core.util.SpringContextHolder;
import cn.yiidii.pigeon.common.strategy.annotation.HandlerType;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 快手去水印实现
 *
 * @author YiiDii Wang
 * @create 2021-08-05 20:58
 */
@HandlerType(bizCode = WaterMarkConstant.KUAISHOU_RM_WATER_MARK, beanName = WaterMarkConstant.KUAISHOU_RM_WATER_MARK)
@Component(WaterMarkConstant.KUAISHOU_RM_WATER_MARK)
public class KuaiShouHandler implements BaseRmWaterMarkHandler<RmWaterMarkVO> {

    /**
     * 去水印
     *
     * @param linkList 视频链接
     * @return 去水印VO
     */
    @Override
    public List<RmWaterMarkVO> remove(List<String> linkList) {
        ThreadPoolTaskExecutor asyncExecutor = SpringContextHolder.getBean("asyncExecutor", ThreadPoolTaskExecutor.class);
        List<RmWaterMarkVO> result = Lists.newArrayList();
        List<CompletableFuture<RmWaterMarkVO>> list = new ArrayList<>();
        for (String l : linkList) {
            CompletableFuture<RmWaterMarkVO> rmWaterMarkVOCompletableFuture = CompletableFuture.supplyAsync(() -> getSingleInfo(l), asyncExecutor)
                    .whenComplete((data, throwable) -> {
                        if (Objects.isNull(throwable)) {
                            result.add(data);
                        }
                    });
            list.add(rmWaterMarkVOCompletableFuture);
        }
        CompletableFuture[] completableFutures = list.toArray(new CompletableFuture[0]);
        // 阻塞等待所有线程执行完成
        CompletableFuture.allOf(completableFutures).join();
        return result;
    }

    /**
     * 获取单个
     *
     * @param shortLink 视频短链接
     * @return 去水印VO
     */
    private RmWaterMarkVO getSingleInfo(String shortLink) {
        // 请求
        HttpResponse resp = HttpRequest.get(shortLink).header(HttpHeaders.USER_AGENT, WaterMarkConstant.MOBILE_UA).execute();
        // 这里KS会重定向, 拿到Location再次请求
        String redirectUrl = resp.header("Location");
        resp = HttpRequest.get(redirectUrl).header(HttpHeaders.USER_AGENT, WaterMarkConstant.MOBILE_UA).execute();
        String body = resp.body();
        Document doc = Jsoup.parse(body);
        // 正则取出KS ID
        String kid = ReUtil.get("\"(kwaiId)\":\"(.*?)\"", body, 0);
        kid = kid.substring(10, kid.length() - 1);
        // video标签的src为视频地址
        String videoUrl = doc.getElementsByTag("video").get(0).attr("src");
        // 从auth-avatar的style取出avatar
        String avatarStyle = doc.getElementsByClass("auth-avatar").get(0).attr("style");
        String avatar = avatarStyle.substring(21, avatarStyle.length() - 1);
        // poster-content
        String posterStyle = doc.getElementsByClass("poster-content").get(0).attr("style");
        String poster = posterStyle.substring(21, posterStyle.length() - 23);
        // 从auth-name取出author
        String author = doc.getElementsByClass("auth-name").get(0).text();
        // 从caption-container取出视频描述
        String videoDesc = doc.getElementsByClass("caption-container").get(0).text();
        Item video = Item.builder()
                .desc(videoDesc)
                .poster(Lists.newArrayList(poster))
                .urlList(Lists.newArrayList(videoUrl))
                .build();
        return RmWaterMarkVO.builder()
                .randomId(RandomUtil.randomString(12))
                .shortId(kid)
                .author(author)
                .avatar(avatar)
                .signature("")
                .video(video)
                .build();
    }
}
