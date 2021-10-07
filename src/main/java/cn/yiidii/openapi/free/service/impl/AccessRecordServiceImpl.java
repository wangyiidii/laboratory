package cn.yiidii.openapi.free.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.yiidii.openapi.common.util.IpUtil;
import cn.yiidii.openapi.common.util.UaUtil;
import cn.yiidii.openapi.common.util.UaUtil.DeviceInfo;
import cn.yiidii.openapi.free.mapper.AccessRecordMapper;
import cn.yiidii.openapi.free.model.bo.system.AccessOverviewBO;
import cn.yiidii.openapi.free.model.bo.system.AccessRecordBO;
import cn.yiidii.openapi.free.model.bo.system.AccessTrendBO;
import cn.yiidii.openapi.free.model.entity.system.AccessRecord;
import cn.yiidii.openapi.free.model.form.AccessRecordForm;
import cn.yiidii.openapi.free.model.vo.AccessOverviewVO;
import cn.yiidii.openapi.free.service.IAccessRecordService;
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
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public AccessRecord addOne(AccessRecordForm form) {
        String uaStr = form.getUaStr();
        uaStr = StrUtil.isEmpty(uaStr) ? request.getHeader(HttpHeaders.USER_AGENT) : uaStr;
        form.setUaStr(uaStr);
        AccessRecord record = new AccessRecord();
        BeanUtils.copyProperties(form, record);

        UserAgent ua = UserAgentUtil.parse(uaStr);
        String ip = ipUtil.getIpAddr(request);
        String loc = ipUtil.getLocationByIp(ip);
        DeviceInfo deviceInfo = UaUtil.getDeviceInfo(uaStr);
        record.setIp(ip)
                .setLoc(loc)
                .setOs(ua.getOs().getName())
                .setPlatform(ua.getPlatform().getName())
                .setBrowser(ua.getBrowser().getName())
                .setVersion(ua.getVersion())
                .setDeviceName(deviceInfo.getName())
                .setDeviceModel(deviceInfo.getModel())
                .setDeviceVersion(deviceInfo.getVersion())
                .setEngine(ua.getEngine().getName())
                .setEngineVersion(ua.getEngineVersion())
                .setCreateTime(LocalDateTime.now());
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
    public AccessOverviewVO getAccessOverview(String path) {
        Date d = new Date();
        final ZoneId zone = ZoneId.systemDefault();
        // 今
        LocalDateTime start = LocalDateTime.ofInstant(DateUtil.beginOfDay(d).toJdkDate().toInstant(), zone);
        LocalDateTime end = LocalDateTime.ofInstant(DateUtil.endOfDay(d).toJdkDate().toInstant(), zone);
        AccessOverviewBO todayData = this.getBaseMapper().getAccessOverview(path, start, end);

        // 昨
        d = DateUtil.offsetDay(d, -1).toJdkDate();
        start = LocalDateTime.ofInstant(DateUtil.beginOfDay(d).toJdkDate().toInstant(), zone);
        end = LocalDateTime.ofInstant(DateUtil.endOfDay(d).toJdkDate().toInstant(), zone);
        AccessOverviewBO yesterdayData = this.getBaseMapper().getAccessOverview(path, start, end);

        // 总
        AccessOverviewBO allData = this.getBaseMapper().getAccessOverview(path, null, null);

        return AccessOverviewVO.builder().today(todayData).yesterday(yesterdayData).all(allData).build();
    }
}
