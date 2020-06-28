package com.neusoft.bookstore.cate.mapper;

import com.neusoft.bookstore.cate.model.Cate;
import com.neusoft.bookstore.menu.model.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/30 9:46
 */
@Mapper
public interface CateMapper {
    Cate findCateByParentCateCodeAndName(Cate cate);

    int insertCate(Cate cate);

    int updateCateByCode(Cate cate);

    List<Cate> listCates();

    Cate findCateByCode(String cateCode);

    List<Cate> findChildCates(String cateCode);

    int deleteCateByCode(String cateCode);

    int findGoodsByCateCode(String cateCode);
}
