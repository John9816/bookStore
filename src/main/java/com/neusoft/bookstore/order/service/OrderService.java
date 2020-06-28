package com.neusoft.bookstore.order.service;

import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderVo;
import com.neusoft.bookstore.util.ResponseVo;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/13 10:47
 */
public interface OrderService {
    ResponseVo addOrder(List<OrderVo> orderVos);

    ResponseVo listOrders(Order order);

    ResponseVo findOrderByOrderCode(String orderCode);

    ResponseVo findAllOrderForApp(String loginAccount, Integer pageSize, Integer pageNum);
}
