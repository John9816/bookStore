package com.neusoft.bookstore.shoppingcar.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/12 9:10
 */
@Data
public class ShoppingCar extends BaseModel {
    @ApiModelProperty("商品编码")
    private String skuCode;
    @ApiModelProperty("商品购买数量")
    private Integer shopNum;
    @ApiModelProperty("商家编码")
    private String businessCode;
    @ApiModelProperty("购买人app登录用户id")
    private Integer orderUserId;
    @ApiModelProperty("商品名称")
    private String skuName;
    @ApiModelProperty("销售价格")
    private Integer salePrice;
    @ApiModelProperty("商品图片")
    private List<String> images;






}
