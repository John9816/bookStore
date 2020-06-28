package com.neusoft.bookstore.menu.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/27 11:33
 */
@Data
public class Menu extends BaseModel {
    @ApiModelProperty("菜单名称")
    private String menuName;
    @ApiModelProperty("菜单编码")
    private String menuCode;
    @ApiModelProperty("菜单类型1：目录 2：菜单")
    private Integer menuType;
    @ApiModelProperty("菜单路由")
    private String menuUrl;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("父级菜单编码(取得是父级菜单的菜单编码)")
    private String parentMenuCode;
    @ApiModelProperty("创建子级时，前端点击的菜单的菜单编码")
    private String frontMenuCode;

}
