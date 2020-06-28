package com.neusoft.bookstore.order.controller;

import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderVo;
import com.neusoft.bookstore.order.service.OrderService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.GoodsInfoException;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/13 10:46
 */
@RestController
@RequestMapping("order")
@Api("order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 订单创建
     * @param orderVos
     * @return
     */
    @ApiOperation(value = "订单创建",notes = "订单创建")
    @PostMapping("addOrder")
    public ResponseVo addOrder(@RequestBody List<OrderVo> orderVos){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = orderService.addOrder(orderVos);
        }catch (Exception e){
            throw e;
        }
        return responseVo;
    }
/**
 * 后台： 2个接口：
 *    1：查询所有订单：带分页和模糊查询
 *    2：查询该订单下的商品信息。
 * app：1个接口：
 *    查询该用户所有的订单遗迹订单下的商品信息
 */

    /**
     * 订单查询
     *
     * @return
     */
    @ApiOperation(value = "订单查询",notes = "订单查询")
    @PostMapping("listOrder")
    public ResponseVo listOrders(@RequestBody Order order){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = orderService.listOrders(order);
        }catch (Exception e){
            throw e;
        }
        return responseVo;
    }

    /**
     * 订单详情查询
     * @param orderCode
     * @return
     */
    @ApiOperation(value = "订单详情查询",notes = "订单详情查询")
    @GetMapping("findOrderDetails")
    public ResponseVo findOrderByOrderCode(String orderCode){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = orderService.findOrderByOrderCode(orderCode);
        }catch (Exception e){
            throw e;
        }
        return responseVo;
    }


    /**
     * App订单记录查询
     * @param loginAccount
     * @param pageSize
     * @param pageNum
     * @return
     */
    @ApiOperation(value = "App订单记录查询",notes = "App订单记录查询")
    @GetMapping("findAllOrderForApp")
    public ResponseVo findAllOrderForApp(String loginAccount,Integer pageSize,Integer pageNum){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = orderService.findAllOrderForApp(loginAccount,pageSize,pageNum);
        }catch (Exception e){
            throw e;
        }
        return responseVo;
    }


}
