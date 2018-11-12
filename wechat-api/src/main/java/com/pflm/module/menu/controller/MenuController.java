package com.pflm.module.menu.controller;
import com.pflm.module.BaseController;
import com.pflm.module.menu.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信菜单控制器
 * @author qinxuewu
 * @create 18/11/10下午1:17
 * @since 1.0.0
 */

@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {

    public  final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public  MenuService menuService;


}
