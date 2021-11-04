package cn.yiidii.openapi.free.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.yiidii.openapi.free.model.entity.system.Dictionary;
import cn.yiidii.openapi.free.model.form.system.DictionarySaveForm;
import cn.yiidii.openapi.free.model.vo.system.DictionaryVO;
import cn.yiidii.openapi.free.service.IDictionaryService;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.util.DozerUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典 Controller
 *
 * @author YiiDii Wang
 * @create 2021-07-31 23:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/free/dict")
@Api(tags = "字典")
public class DictionaryController {

    private final IDictionaryService dictionaryService;
    private final DozerUtils dozerUtils;

    @GetMapping("")
    @ApiOperation(value = "根据类型查询字典值")
    private R<List<DictionaryVO>> list(@RequestParam @NotNull(message = "type不能为空") String type) {
        List<Dictionary> list = dictionaryService.list(Wrappers.<Dictionary>lambdaQuery().eq(Dictionary::getType, type).orderByAsc(Dictionary::getSortValue));
        List<DictionaryVO> dictVOs = dozerUtils.mapList(list, DictionaryVO.class);
        return R.ok(dictVOs);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存")
    private R<DictionaryVO> save(@RequestBody DictionarySaveForm form) {
        Dictionary dict = dictionaryService.save(form);
        return R.ok(dozerUtils.map(dict, DictionaryVO.class));
    }

}
