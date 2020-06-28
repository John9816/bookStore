package com.neusoft.bookstore.cate.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.security.auth.message.callback.PrivateKeyCallback;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/30 9:24
 */
@Data
public class Cate extends BaseModel {
    @ApiModelProperty("分类名称")
    private String cateName;
    @ApiModelProperty("分类编码")
    private String cateCode;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("父级分类编码")
    private String parentCateCode;
    @ApiModelProperty("创建子级时，前端点击的分类的分类编码")
    private String frontCateCode;

}
