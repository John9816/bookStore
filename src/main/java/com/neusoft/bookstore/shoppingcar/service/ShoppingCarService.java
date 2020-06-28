package com.neusoft.bookstore.shoppingcar.service;

import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.util.ResponseVo;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/12 9:08
 */
public interface ShoppingCarService {
    ResponseVo addShoppingCar(ShoppingCar shoppingCar);

    ResponseVo findGoodsFromCar(Integer userId, Integer pageNum, Integer pageSize);

    ResponseVo deleteGoodsFromCar(ShoppingCar shoppingCar);
}
