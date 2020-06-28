package com.neusoft.bookstore.customer.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/23 11:10
 */
@Data
public class Customer extends BaseModel {

    @ApiModelProperty("用户登录账户")
    private String userAccount;
    @ApiModelProperty("用户名称")
    private String userName;
    @ApiModelProperty("用户性别0女1男2未知")
    private Integer userSex;
    @ApiModelProperty("用户手机号")
    private String phone;
    @ApiModelProperty("用户邮箱")
    private String email;
    @ApiModelProperty("身份证")
    private String idCard;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("积分")
    private BigDecimal score;
    @ApiModelProperty("登录源标记0:App 1:PC")
    private Integer isAdmin;
    @ApiModelProperty("前台获取积分")
    private String frontScore;




}
