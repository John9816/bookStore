package com.neusoft.bookstore.picture.controller;

import com.neusoft.bookstore.picture.model.Picture;
import com.neusoft.bookstore.picture.service.PictureService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/11 09:20
 */
@Api("轮播图")
@RestController
@RequestMapping("picture")
public class PictureController {
    @Autowired
    private PictureService pictureService;

    @ApiOperation(value = "轮播图新增",notes = "轮播图新增")
    @PostMapping("addPic")
    public ResponseVo addPic(@RequestBody Picture picture){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = pictureService.addPic(picture);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "轮播图列表查询",notes = "轮播图列表查询")
    @PostMapping("listPic")
    public ResponseVo listPic(@RequestBody Picture picture){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = pictureService.listPic(picture);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "轮播图状态更新",notes = "轮播图状态更新")
    @PostMapping("updatePic")
    public ResponseVo updatePic(@RequestBody Picture picture){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = pictureService.updatePic(picture);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }
}
