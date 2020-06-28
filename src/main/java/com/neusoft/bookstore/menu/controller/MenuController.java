package com.neusoft.bookstore.menu.controller;

import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.menu.model.Menu;
import com.neusoft.bookstore.menu.service.MenuService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/27 11:20
 */
@Api("menu")
@RestController
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("addMenu")
    @ApiOperation(value = "新增菜单",notes = "新增菜单")
    private ResponseVo addMenu(@RequestBody Menu menu){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = menuService.addMenu(menu);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "菜单树查询",notes = "菜单树查询")
    @GetMapping("listMenuTree")
    public ResponseVo listMenuTree(){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = menuService.listMenuTree();
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "菜单详情查询",notes = "菜单详情查询")
    @GetMapping("findMenuByCode")
    public ResponseVo findMenuByCode(String menuCode){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = menuService.findMenuByCode(menuCode);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "修改菜单信息",notes = "修改菜单信息")
    @PostMapping("updateMenuByCode")
    public ResponseVo updateMenuByCode(@RequestBody Menu menu){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = menuService.updateMenuByCode(menu);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }


    @ApiOperation(value = "删除菜单信息",notes = "删除菜单信息")
    @GetMapping("deleteMenuByCode")
    public ResponseVo deleteMenuByCode(String menuCode){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = menuService.deleteMenuByCode(menuCode);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }
}
