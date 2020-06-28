package com.neusoft.bookstore.util;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/15 8:56
 */
public class GoodsInfoException extends RuntimeException{
    public GoodsInfoException(){
        super();
    }
    public GoodsInfoException(String messgae){
        super(messgae);
    }

    public GoodsInfoException(String messgae,Throwable cause){
        super(messgae,cause);
    }

    public GoodsInfoException(String messgae,Throwable cause,boolean enableSuppression,boolean writableStackTrace){
        super(messgae,cause,enableSuppression,writableStackTrace);
    }

}
