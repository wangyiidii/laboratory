package cn.yiidii.openapi.free.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yiidii.openapi.free.model.vo.WeiBoHotVO;
import cn.yiidii.pigeon.common.core.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * FreeController
 *
 * @author YiiDii Wang
 * @create 2021-10-08 13:00
 */
@Api(tags = "免费接口")
@Slf4j
@RestController
@RequestMapping("/free")
@RequiredArgsConstructor
public class FreeController {

    @GetMapping("/wb/hot")
    @ApiOperation("微博热搜topN")
    public R<?> miBrushStepTopN(@RequestParam(defaultValue = "5") Integer topN) {
        return R.ok(getWeiBoHot().stream().limit(topN + 1));
    }

    /**
     * 微博热搜
     *
     * @return 微博热搜vo
     */
    public static List<WeiBoHotVO> getWeiBoHot() {
        // 请求
        String domain = "https://s.weibo.com";
        HttpResponse resp = HttpRequest.post(domain + "/top/summary?cate=realtimehot")
                .header(Header.COOKIE, "SINAGLOBAL=1409856127723.65.1619601446235; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9WhiL82Pi6k1RPUKk4laF.3L; _s_tentry=www.baidu.com; UOR=,,www.baidu.com; Apache=8332062084934.851.1636900102448; ULV=1636900102515:5:1:1:8332062084934.851.1636900102448:1631083674515; SUB=_2AkMWzay9f8NxqwJRmPATy23kb4h_ygHEieKgkV1mJRMxHRl-yj9jqm0jtRB6PU2CUgc7bT-2h_JHTyHZZOeM7s_qYc-x")
                .execute();
        // jsoup解析
        Document doc = Jsoup.parse(resp.body());
        Element tbody = doc.getElementsByTag("tbody").get(0);
        Elements trs = tbody.getElementsByTag("tr");
        List<WeiBoHotVO> weiBoHotVOS = trs.stream().map(tr -> {
            Elements tds = tr.getElementsByTag("td");
            Elements i = tds.get(0).getElementsByTag("i");
            // index
            Integer index = -1;
            // hotValue
            Long hotValue = -1L;
            if (CollUtil.isEmpty(i)) {
                try {
                    index = Integer.valueOf(tds.get(0).text());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            // 关键字
            String keyword = tds.get(1).getElementsByTag("a").get(0).text();
            // 链接
            String href = domain + tds.get(1).getElementsByTag("a").get(0).attr("href");
            if (index > 0) {
                String hotValueStr = tds.get(1).getElementsByTag("span").get(0).text();
                hotValue = Long.parseLong(ReUtil.get("[0-9]+", hotValueStr, 0));
            }
            // tag
            String tag = tds.get(2).text();
            return new WeiBoHotVO()
                    .setRank(index)
                    .setKeyword(keyword)
                    .setHref(href)
                    .setHotValue(hotValue)
                    .setTag(tag);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return weiBoHotVOS;
    }

}
