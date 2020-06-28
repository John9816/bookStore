package com.neusoft.bookstore.customer.controller;

import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.customer.service.CustomerService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/23 11:03
 */
@RestController
@RequestMapping("customer")
@Api("customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;


    @PostMapping("addCustomer")
    @ApiOperation(value = "新增用户")
    private ResponseVo addCustomer(@RequestBody Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.addCustomer(customer);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }


    @ApiOperation(value = "app和pc端用户登录",notes = "app和pc端用户登录")
    @PostMapping("login")
    public ResponseVo login(@RequestBody Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.login(customer);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }


    @ApiOperation(value = "app和pc端用户退出",notes = "app和pc端用户退出")
    @GetMapping("loginOut")
    public ResponseVo loginOut(String userAccount){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.loginOut(userAccount);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "用户列表查询",notes = "用户列表查询")
    @PostMapping("listCustomer")
    public ResponseVo listCustomers(@RequestBody Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.listCustomers(customer);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "用户详情查询",notes = "用户详情查询")
    @GetMapping("findCustomerById")
    public ResponseVo findCustomerById(Integer id){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.findCustomerById(id);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "根据Id修改用户信息",notes = "根据Id修改用户信息")
    @PostMapping("updateCustomerById")
    public ResponseVo updateCustomerById(@RequestBody Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.updateCustomerById(customer);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }


    @ApiOperation(value = "根据Id删除用户信息",notes = "根据Id删除用户信息")
    @GetMapping("deleteCustomerById")
    public ResponseVo deleteCustomerById(Integer id){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.deleteCustomerById(id);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }


    /**
     *
     * @param originPwd 原始密码
     * @param newPwd 新密码
     * @param userId 用户id
     * @param userAccount 登陆人Id
     * @return
     */
    @ApiOperation(value = "根据旧密码更新密码",notes = "根据旧密码更新密码")
    @GetMapping("updatePwd")
    public ResponseVo updatePwd(String originPwd,String newPwd,Integer userId,String userAccount){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.updatePwd(originPwd,newPwd,userId,userAccount);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }


    /**
     *
     * @param frontScore 前台充值积分
     * @param id  充值用户id
     * @param loginAccount 登录PC或者APP用户
     * @return
     */
    @ApiOperation(value = "更新积分",notes = "更新积分")
    @GetMapping("updateScore")
    public ResponseVo updateScore(String frontScore, Integer id,String loginAccount){
        ResponseVo responseVo = new ResponseVo();
        try{
            responseVo = customerService.updateScore(frontScore,id,loginAccount);
        }catch (Exception e){
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setMsg("系统异常");
            responseVo.setSuccess(false);
            e.printStackTrace();
        }
        return responseVo;
    }



}
