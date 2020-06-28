package com.neusoft.bookstore.cate.service;

import com.neusoft.bookstore.cate.model.Cate;
import com.neusoft.bookstore.util.ResponseVo;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/30 9:44
 */
public interface CateService {
    ResponseVo addCate(Cate cate);

    ResponseVo updateCateByCode(Cate cate);

    ResponseVo listCateTree();

    ResponseVo findCateByCode(String cateCode);

    ResponseVo deleteCateByCode(String cateCode);

    ResponseVo findCateByCateCode(String cateCode);
}
