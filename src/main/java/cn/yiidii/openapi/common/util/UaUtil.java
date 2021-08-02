package cn.yiidii.openapi.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.pigeon.common.core.constant.StringPool;
import com.alibaba.fastjson.JSONObject;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User-Agent工具
 *
 * @author YiiDii Wang
 * @create 2021-08-01 15:41
 */
@Slf4j
public class UaUtil {

    private static final String UNKNOWN = "UNKNOWN";
    private static final String WIN = "win";
    private static final String ANDROID = "Android";
    private static final String IPHONE = "iPhone";
    private static final String IPAD = "iPad";
    private static final String MAC = "Macintosh";

    private static final String REGX_WIN = "Windows\\s+\\w+\\s+[0-9\\.]+";
    private static final String REGX_MAC = "[\\w\\s]+Mac.+;";

    /**
     * 从ua获取设备信息
     *
     * @param uaStr ua
     * @return DeviceInfo
     */
    public static DeviceInfo getDeviceInfo(String uaStr) {
        // 取出括号里的内容, eg: Linux; Android 10; ONEPLUS A6000 Build/QKQ1.190716.003; wv
        String all = ReUtil.get("(?<=\\()[^\\)]+", uaStr, 0);
        DeviceInfo info = new DeviceInfo();
        try {
            if (StrUtil.isBlank(all)) {
                // blank
                return info;
            } else if (StrUtil.containsAnyIgnoreCase(all, WIN)) {
                // windows
                String[] win = ReUtil.get(REGX_WIN, all, 0).split(" ");
                info.setName(win[0]);
                info.setVersion(win[2]);
            } else if (StrUtil.containsAnyIgnoreCase(all, IPHONE)) {
                // iPhone, iPhone无法获取版本号
                info.setName(IPHONE);
            } else if (StrUtil.containsAnyIgnoreCase(all, ANDROID)) {
                // Android
                String[] split = all.split("; ");
                String[] deviceArr = split[2].split(" ");
                info.setName(CollUtil.join(Arrays.asList(deviceArr[0], deviceArr[1]), " "));
                info.setVersion(split[1]);
            } else if (StrUtil.containsAnyIgnoreCase(all, MAC)) {
                // Mac
                String macInfo = ReUtil.get(REGX_MAC, all, 0);
                macInfo = macInfo.substring(0, macInfo.length() - 2);
                String[] mac = macInfo.split(" ");
                info.setName(mac[1]);
                info.setVersion(mac[4]);
            } else if (StrUtil.containsAnyIgnoreCase(all, IPAD)) {
                info.setName(IPAD);
            }
        } catch (Exception exception) {
            return info;
        }

        // name model后处理
        String name = info.getName();
        if (!StrUtil.equals(name, UNKNOWN) && name.contains(StringPool.SPACE)) {
            String[] split = name.split(" ");
            List<String> list = CollUtil.toList(split);
            list.remove(0);
            info.setName(split[0]);
            info.setModel(CollUtil.join(list, " "));
        }
        // version 后处理
        String version = info.getVersion();
        if (!StrUtil.equals(version, UNKNOWN)) {
            version = ReUtil.get("[\\d|_|-|\\\\.]+", version, 0).replaceAll("_", ".");
            info.setVersion(version);
        }
        return info;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceInfo {

        private String name = UNKNOWN;
        private String model = UNKNOWN;
        private String version = UNKNOWN;

    }

    public static void main(String[] args) {
        String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Mobile/15E148 Safari/604.1";
        final DeviceInfo deviceInfo = getDeviceInfo(ua);
        System.out.println(JSONObject.toJSONString(deviceInfo));
    }

}
