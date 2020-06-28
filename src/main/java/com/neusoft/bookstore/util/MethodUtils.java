package com.neusoft.bookstore.util;

import com.neusoft.bookstore.customer.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/28 9:29
 */
@Component
public class MethodUtils {
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    public String getCreateBy(String loginAccount) {
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if(customerByRedis == null){
            return "admin";
        }else {
            return customerByRedis.getUserAccount();
        }
    }


}
