package com.neusoft.bookstore.util;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/15 9:19
 */

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 处理controller层
 *
 * 返回前端
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)//表明处理的异常类型
    public ResponseVo handlerException(Exception e){
        ResponseVo responseVo = new ResponseVo(false,ErrorCode.FAIL,"系统异常");
        //处理自定义异常
        if(e instanceof GoodsInfoException){
            responseVo.setMsg(e.getMessage());
        }
            return responseVo;

    }
}
