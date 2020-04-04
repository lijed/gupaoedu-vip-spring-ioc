package com.gupaoedu.vip.demo.service.impl;

import com.gupaoedu.vip.demo.service.IModifyService;
import com.gupaoedu.vip.spring.framework.annotation.GPService;
import lombok.extern.slf4j.Slf4j;

/**
 * 增删改业务
 *
 * @author Tom
 */
@GPService
@Slf4j
public class ModifyService implements IModifyService {

    /**
     * 增加
     */
    public String add(String name, String addr) {
        log.info("Add method is invoke with parameters: [name: " + name + ", addr:" + addr + "]");
        return "modifyService add,name=" + name + ",addr=" + addr;
    }

    /**
     * 修改
     */
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * 删除
     */
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }

}
