package com.neusoft.bookstore.goods.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/7 8:30
 */
@Data
public class Goods extends BaseModel {

    @ApiModelProperty("商品名称")
    private String skuName;
    @ApiModelProperty("标准书号")
    private String isbn;
    @ApiModelProperty("一级商品分类")
    private String firstCateCode;
    @ApiModelProperty("二级商品分类")
    private String secondCateCode;
    @ApiModelProperty("广告词")
    private String skuAd;
    @ApiModelProperty("商品介绍")
    private String skuIntroduction;
    @ApiModelProperty("商家编码")
    private String businessCode;
    @ApiModelProperty("库存")
    private Integer storeNum;
    @ApiModelProperty("定价")
    private BigDecimal costPrice;
    @ApiModelProperty("售价")
    private BigDecimal salePrice;
    @ApiModelProperty("商品状态0:在售；1:已下架；2:未发布")
    private Integer skuStatus;
    @ApiModelProperty("商品编码")
    private String  skuCode;
    @ApiModelProperty("已售数量")
    private Integer saleNum;
    @ApiModelProperty("商品上架时间")
    private Date saleTime;

    @ApiModelProperty("从前端获取定价")
    private String frontCostPrice;
    @ApiModelProperty("从前端获取售价")
    private String frontSalePrice;

    @ApiModelProperty("商品图片")
    private List<String> images;


}
