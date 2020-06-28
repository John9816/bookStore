package com.neusoft.bookstore.order.mapper;

import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderDetail;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/13 10:48
 */
@Mapper
public interface OrderMapper {

    void addOrder(Order order);

    void addOrderDetail(OrderDetail orderDetail);

    List<Order> listOrders(Order order);

    Order findOrderByOrderCode(String orderCode);

    List<Order> findAllOrderForApp(String loginAccount);
}
