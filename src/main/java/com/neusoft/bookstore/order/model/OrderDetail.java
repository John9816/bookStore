package com.neusoft.bookstore.order.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/13 10:49
 */
@Data
public class OrderDetail extends BaseModel {
    @ApiModelProperty("购买数量")
    private Integer shopNum;
    @ApiModelProperty("订单编码")
    private String orderCode;
    @ApiModelProperty("商品编码")
    private String skuCode;
    @ApiModelProperty("该商品总金额")
    private BigDecimal skuAmount;

}
