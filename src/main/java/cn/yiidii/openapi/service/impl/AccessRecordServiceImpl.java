package cn.yiidii.openapi.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.yiidii.openapi.common.util.IpUtil;
import cn.yiidii.openapi.common.util.UaUtil;
import cn.yiidii.openapi.common.util.UaUtil.DeviceInfo;
import cn.yiidii.openapi.mapper.AccessRecordMapper;
import cn.yiidii.openapi.model.bo.system.AccessOverviewBO;
import cn.yiidii.openapi.model.bo.system.AccessRecordBO;
import cn.yiidii.openapi.model.bo.system.AccessTrendBO;
import cn.yiidii.openapi.model.entity.system.AccessRecord;
import cn.yiidii.openapi.model.vo.AccessOverviewVO;
import cn.yiidii.openapi.service.IAccessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * 访问记录业务实现
 *
 * @author YiiDii Wang
 * @create 2021-07-31 23:33
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccessRecordServiceImpl extends ServiceImpl<AccessRecordMapper, AccessRecord> implements IAccessRecordService {

    private final IpUtil ipUtil;
    private final HttpServletRequest request;

    @Override
    public AccessRecord addOne(String uaStr) {
        uaStr = StrUtil.isEmpty(uaStr) ? request.getHeader(HttpHeaders.USER_AGENT) : uaStr;
        UserAgent ua = UserAgentUtil.parse(uaStr);
        String ip = ipUtil.getIpAddr(request);
        String loc = ipUtil.getLocationByIp(ip);
        DeviceInfo deviceInfo = UaUtil.getDeviceInfo(uaStr);
        AccessRecord record = AccessRecord.builder()
                .ip(ip)
                .loc(loc)
                .os(ua.getOs().getName())
                .platform(ua.getPlatform().getName())
                .browser(ua.getBrowser().getName())
                .version(ua.getVersion())
                .deviceName(deviceInfo.getName())
                .deviceModel(deviceInfo.getModel())
                .deviceVersion(deviceInfo.getVersion())
                .engine(ua.getEngine().getName())
                .engineVersion(ua.getEngineVersion())
                .uaStr(uaStr)
                .createTime(LocalDateTime.now())
                .build();
        this.save(record);
        return record;
    }

    @Override
    public List<AccessRecordBO> statistic(String group, Integer topN) {
        // 默认按照平台和浏览器分组
        group = StrUtil.isEmpty(group) ? "platform,browser" : group;
        // topN默认值
        topN = Objects.nonNull(topN) ? topN : 10;
        return this.getBaseMapper().statistics(Arrays.asList(group.split(",")), topN);
    }

    @Override
    public List<AccessTrendBO> accessTrend() {
        List<AccessTrendBO> accessTrendBOList = this.getBaseMapper().accessTrend();
        final Map<Integer, AccessTrendBO> accessTrendBOMap = accessTrendBOList.stream().collect(Collectors.toMap(AccessTrendBO::getHour, e -> e, (e1, e2) -> e2));
        for (int i = 1; i <= 24; i++) {
            if (!accessTrendBOMap.containsKey(i)) {
                accessTrendBOMap.put(i, new AccessTrendBO(i, 0));
            }
        }
        accessTrendBOList = accessTrendBOMap.values()
                .stream()
                .sorted(Comparator.comparing(AccessTrendBO::getHour))
                .collect(Collectors.toList());
        return accessTrendBOList;
    }

    /**
     * 访问趋势图
     *
     * @return AccessTrendBO
     */
    @Override
    public AccessOverviewVO  getAccessOverview() {
        Date d = new Date();
        final ZoneId zone = ZoneId.systemDefault();
        LocalDateTime start = LocalDateTime.ofInstant(DateUtil.beginOfDay(d).toJdkDate().toInstant(), zone);
        LocalDateTime end = LocalDateTime.ofInstant(DateUtil.endOfDay(d).toJdkDate().toInstant(), zone);
        AccessOverviewBO todayData = this.getBaseMapper().getAccessOverview(start, end);

        d = DateUtil.offsetDay(d, -1).toJdkDate();
        start = LocalDateTime.ofInstant(DateUtil.beginOfDay(d).toJdkDate().toInstant(), zone);
        end = LocalDateTime.ofInstant(DateUtil.endOfDay(d).toJdkDate().toInstant(), zone);
        AccessOverviewBO yesterdayData = this.getBaseMapper().getAccessOverview(start, end);

        AccessOverviewBO allData = this.getBaseMapper().getAccessOverview(start, end);

        return AccessOverviewVO.builder().today(todayData).yesterday(yesterdayData).all(allData).build();
    }
}
