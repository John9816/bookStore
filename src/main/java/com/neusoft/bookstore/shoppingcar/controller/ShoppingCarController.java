package com.neusoft.bookstore.shoppingcar.controller;

import com.neusoft.bookstore.picture.model.Picture;
import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.shoppingcar.service.ShoppingCarService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/12 9:06
 */
@Api("shoppingcar")
@RestController
@RequestMapping("shoppingcar")
public class ShoppingCarController {
    @Autowired
    private ShoppingCarService shoppingCarService;


    /**
     * 添加商品到购物车
     * @param shoppingCar
     * @return
     */
    @ApiOperation(value = "添加商品到购物车",notes = "添加商品到购物车")
    @PostMapping("addShoppingCar")
    public ResponseVo addShoppingCar(@RequestBody ShoppingCar shoppingCar){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = shoppingCarService.addShoppingCar(shoppingCar);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "购物车列表",notes = "购物车列表")
    @GetMapping("findGoodsFromCar")
    public ResponseVo addShoppingCar(Integer userId,Integer pageSize,Integer pageNum){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = shoppingCarService.findGoodsFromCar(userId,pageNum,pageSize);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 删除购物车商品
     * @param shoppingCar
     * @return
     */
    @ApiOperation(value = "删除购物车商品",notes = "删除购物车商品")
    @PostMapping("deleteGoodsFromCar")
    public ResponseVo deleteGoodsFromCar(@RequestBody ShoppingCar shoppingCar){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = shoppingCarService.deleteGoodsFromCar(shoppingCar);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

}
