package com.neusoft.bookstore.menu.service;

import com.neusoft.bookstore.menu.model.Menu;
import com.neusoft.bookstore.util.ResponseVo;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/27 11:21
 */
public interface MenuService {
    ResponseVo addMenu(Menu menu);

    ResponseVo listMenuTree();

    ResponseVo findMenuByCode(String menuCode);

    ResponseVo updateMenuByCode(Menu menu);

    ResponseVo deleteMenuByCode(String menuCode);
}
