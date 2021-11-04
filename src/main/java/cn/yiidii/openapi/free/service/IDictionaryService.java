package cn.yiidii.openapi.free.service;

import cn.yiidii.openapi.free.model.entity.system.Dictionary;
import cn.yiidii.openapi.free.model.form.system.DictionarySaveForm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 字典 业务接口
 *
 * @author YiiDii Wang
 * @create 2021-11-03 22:20
 */
public interface IDictionaryService extends IService<Dictionary> {

    /**
     * 保存
     *
     * @param form form
     */
    Dictionary save(DictionarySaveForm form);

}
