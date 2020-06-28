package com.neusoft.bookstore.goods.mapper;

import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.goods.model.GoodsImage;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/7 9:32
 */
@Mapper
public interface GoodsMapper {

    List<Goods> listBusiness();

    Goods findGoodsByCondition(Goods goods);

    int addGoods(Goods goods);

    int addImages(GoodsImage goodsImage);

    List<Goods> listGoods(Goods goods);

    List<String> findImagesBySkuCode(String skuCode);

    Goods findGoodsBySkuCode(String skuCode);

    Map<String, Object> findBusinessByCode(String businessCode);

    int updateGoodsInfo(Goods goods);

    void deleteImages(String skuCode);

    int deleteGoods(String skuCode);

    int updateGoodStatus(String skuCode, String updatedBy, String status);

    void updateGoodsStoreAndSaNum(Map<String, Object> map);
}
