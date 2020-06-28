package com.neusoft.bookstore.customer.service;

import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.util.ResponseVo;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/23 11:04
 */
public interface CustomerService {
    ResponseVo addCustomer(Customer customer);

    ResponseVo login(Customer customer);

    ResponseVo loginOut(String userAccount);

    ResponseVo listCustomers(Customer customer);

    ResponseVo findCustomerById(Integer id);

    ResponseVo updateCustomerById(Customer customer);

    ResponseVo deleteCustomerById(Integer id);

    ResponseVo updatePwd(String originPwd, String newPwd, Integer userId, String userAccount);

    ResponseVo updateScore(String score, Integer id,String loginAccount);
}
