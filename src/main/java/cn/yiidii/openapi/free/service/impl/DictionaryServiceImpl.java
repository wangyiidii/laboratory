package cn.yiidii.openapi.free.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.common.util.AdminNotifyUtil;
import cn.yiidii.openapi.free.mapper.DictionaryMapper;
import cn.yiidii.openapi.free.model.entity.system.Dictionary;
import cn.yiidii.openapi.free.model.form.system.DictionarySaveForm;
import cn.yiidii.openapi.free.model.vo.mail.AdminNotifyVO;
import cn.yiidii.openapi.free.service.IDictionaryService;
import cn.yiidii.pigeon.common.core.base.enumeration.Status;
import cn.yiidii.pigeon.common.core.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 字典 业务实现
 *
 * @author YiiDii Wang
 * @create 2021-07-31 23:33
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements IDictionaryService {

    private final AdminNotifyUtil adminNotifyUtil;

    @Override
    public Dictionary save(DictionarySaveForm form) {
        String type = form.getType();
        String code = form.getCode();
        // 先查询是否支持当前类型
        Dictionary dictType = this.getOne(Wrappers.<Dictionary>lambdaQuery().eq(Dictionary::getType, type), false);
        if (Objects.isNull(dictType)) {
            throw new BizException(StrUtil.format("不支持类型[{}]", type));
        }
        // 再查询code是否存在
        LambdaQueryWrapper<Dictionary> lqw = Wrappers.<Dictionary>lambdaQuery().eq(Dictionary::getType, type)
                .eq(Dictionary::getCode, code);
        Dictionary dict = this.getOne(lqw);
        if (Objects.nonNull(dict)) {
            throw new BizException(StrUtil.format("[{}]已存在", code));
        }
        // 获取最大排序值往后加
        QueryWrapper<Dictionary> qw = new QueryWrapper<>();
        qw.select("max(sort_value) maxSortValue");
        qw.eq("type", type);
        Map<String, Object> map = this.getMap(qw);
        int sortValue = Integer.parseInt(map.getOrDefault("maxSortValue", 1).toString()) + 1;
        Dictionary dictInsert = BeanUtil.toBean(form, Dictionary.class);
        dictInsert.setLabel(dictType.getLabel())
                .setCode(StrUtil.isNotBlank(code) ? code : type + "_" + sortValue)
                .setSortValue(sortValue)
                .setStatus(Status.DISABLED);
        // 插入
        this.save(dictInsert);

        // 通知
        adminNotifyUtil.doNotify("新增字典通知",
                new AdminNotifyVO().setContent(StrUtil.format("有人在Lab新增了字典（{}），快来看看吧！", form.getType())));
        return dictInsert;
    }
}
