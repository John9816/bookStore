package com.neusoft.bookstore.order.model;

import lombok.Data;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/13 11:49
 */
@Data
public class OrderVo {
    /**
     * 新建订单数，接收前端的参数
     */

    private String skuCode;
    private String businessCode;
    private Integer shopNum;
    private String loginAccount;

}
