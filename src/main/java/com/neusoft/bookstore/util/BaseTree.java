package com.neusoft.bookstore.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/29 8:58
 */
@Data
public class BaseTree {
    @ApiModelProperty("树节点名称")
    private String nodeName;
    @ApiModelProperty("树节Id:唯一标识树的节点信息")
    private String nodeId;
    @ApiModelProperty("树节点路径 针对菜单")
    private String nodeUrl;
    @ApiModelProperty("该节点所有的属性信息")
    private Object attribute;
    @ApiModelProperty("该节点所有的子节点信息")
    private List<BaseTree> childNodes;
}
