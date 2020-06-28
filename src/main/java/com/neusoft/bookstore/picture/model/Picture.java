package com.neusoft.bookstore.picture.model;

import com.neusoft.bookstore.util.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/11 09:20
 */
@Data
public class Picture extends BaseModel {

    @ApiModelProperty("图片路径")
    private String picUrl;
    @ApiModelProperty("图片状态")
    private Integer picStatus;
    @ApiModelProperty("图片状态名称")
    private String picStatusName;


}
