package com.neusoft.bookstore.goods.service;

import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/7 9:31
 */
public interface GoodsService {
    ResponseVo listBusiness();

    ResponseVo uploadImage(MultipartFile file);

    ResponseVo addGoods(Goods goods);

    ResponseVo listGoods(Goods goods);

    ResponseVo findGoodsBySkuCode(String skuCode);

    ResponseVo findBusinessByCode(String businessCode);

    ResponseVo updateGoodsInfo(Goods goods);

    ResponseVo deleteGoods(String skuCode, String loginAccount);

    ResponseVo updateGoodStatus(String skuCode, String loginAccount, String status);
}
