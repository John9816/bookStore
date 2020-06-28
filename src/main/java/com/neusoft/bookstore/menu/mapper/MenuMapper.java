package com.neusoft.bookstore.menu.mapper;

import com.neusoft.bookstore.menu.model.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/27 11:22
 */
@Mapper
public interface MenuMapper {
    Menu findMenuByParentMenuCodeAndName(Menu menu);

    Menu findMenuByMenuCode(String frontMenuCode);

    void updateTypeUrlByCode(String parentMenuCode);

    int insertMenu(Menu menu);

    List<Menu> listMenus();

    int updateMenuByCode(Menu menu);

    List<Menu> findChildMenus(String menuCode);

    int deleteMenuByCode(String menuCode);
}
