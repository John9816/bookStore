package com.neusoft.bookstore.order.model;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/13 10:49
 */
@Data
public class Order extends BaseModel {
    @ApiModelProperty("购买人id")
    private Integer orderUserId;
    @ApiModelProperty("订单编码")
    private String orderCode;
    @ApiModelProperty("商家编码")
    private String businessCode;
    @ApiModelProperty("订单状态0:已下单1:已发货2:已完成3:已评价4:已取消")
    private Integer orderStatus;
    @ApiModelProperty("支付状态0:未支付1:已支付2:退款中3:已退款")
    private Integer payStatus;
    @ApiModelProperty("订单总金额")
    private BigDecimal orderAmount;
    @ApiModelProperty("下单人")
    private String orderdPerson;
    @ApiModelProperty("手机号")
    private String frontPhone;

    @ApiModelProperty("商品编码")
    private String  skuCode;
    @ApiModelProperty("商品名称")
    private String skuName;
    @ApiModelProperty("定价")
    private BigDecimal costPrice;
    @ApiModelProperty("售价")
    private BigDecimal salePrice;
    @ApiModelProperty("商品购买数量")
    private Integer shopNum;
    @ApiModelProperty("图片路径")
    private String skuImagesPath;
    @ApiModelProperty("商品总金额")
    private BigDecimal skuAmount;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("账户")
    private String userAccount;
    @ApiModelProperty("手机号")
    private String phone;





}
