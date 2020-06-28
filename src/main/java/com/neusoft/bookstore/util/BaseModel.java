package com.neusoft.bookstore.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/23 11:20
 */
@Data
public class BaseModel implements Serializable {

    @ApiModelProperty("主键id")
    private Integer id;
    @ApiModelProperty("作废标记:0:否1:是")
    private Integer isDelete;
    @ApiModelProperty("创建时间")
    private Date createdTime;
    @ApiModelProperty("创建人(当前登录用户账户，若不存在取固定值\"admin\")")
    private String createdBy;
    @ApiModelProperty("更新时间")
    private Date updatedTime;
    @ApiModelProperty("更新人(当前登录用户账户，若不存在取固定值\"admin\")")
    private String updatedBy;
    @ApiModelProperty("当前页码")
    private Integer pageNum;
    @ApiModelProperty("每页显示的条数")
    private Integer pageSize;
    @ApiModelProperty("前台获取用户账号或手机号")
    private String loginAccount;
}
