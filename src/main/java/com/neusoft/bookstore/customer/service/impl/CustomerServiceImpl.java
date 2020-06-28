package com.neusoft.bookstore.customer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.mapper.CustomerMapper;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.customer.service.CustomerService;
import com.neusoft.bookstore.shoppingcar.mapper.ShoppingCarMapper;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.MD5Util;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/23 11:04
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    /**
     * 用户新增 ：
     * 1： 需要校验前台输入的用户名（用户账号）和手机好在系统中是否唯一
     * 2： 我们需要校验 是app注册 还是pc 注册 用过isAdmin（前台给值） 只需要校验 isAdmin是否规范（0或者1）
     * 3： 用户输入的密码 需要加密  MD5
     * 4：还要处理用户输入的金额（类型转换  String -->BigDecimal）  JSON
     */
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ShoppingCarMapper shoppingCarMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;


    /**
     * 新增用户
     * @param customer
     * @return
     */
    @Override
    public ResponseVo addCustomer(Customer customer) {
        ResponseVo responseVo = new ResponseVo(false,ErrorCode.FAIL,"新增失败");
        String result = "";
        //1： 需要校验前台输入的用户名（用户账号）和手机好在系统中是否唯一
        Customer customerByDb = customerMapper.findCustomerByPhoneAndAccount(customer);
        if(customerByDb != null){
            //有值存在，页面输入有重复
            responseVo.setMsg("注册失败,用户账号或手机号已存在，请检查后重试！");
            return responseVo;
        }
         //2： 我们需要校验 是app注册 还是pc 注册 用过isAdmin（前台给值） 只需要校验 isAdmin是否规范（0或者1）
        Integer isAdmin = customer.getIsAdmin();
        if(!StringUtils.isEmpty(isAdmin)){
            //校验
            if(isAdmin != 0 && isAdmin != 1){
                responseVo.setMsg("注册失败，无法判断app还是pc注册，请检查后重试！");
                return responseVo;

            }
        }else {
            responseVo.setMsg("idAdmin不能为空");
            return responseVo;
        }
        //3： 用户输入的密码 需要加密  MD5
        String password = customer.getPassword();
        String s = MD5Util.inputPassToFormPass(password);
        customer.setPassword(s);
        //4：还要处理用户输入的金额（类型转换  String -->BigDecimal）  JSON
        BigDecimal score = new BigDecimal(customer.getFrontScore());
        customer.setScore(score);
        //5：对创建人 赋值
        //TODO 登录后
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(customer.getLoginAccount());
        if(customerByRedis == null){
            customer.setCreatedBy("admin");
        }else {
            customer.setCreatedBy(customerByRedis.getUserAccount());
        }
        int i = customerMapper.addCustomer(customer);
        if(i != 1){
            responseVo.setMsg("新增失败！");
            return responseVo;
        }
        responseVo.setSuccess(true);
        responseVo.setCode(ErrorCode.SUCCESS);
        responseVo.setMsg("新增成功！");
        return responseVo;
    }
    /**
     * 登录：1.pc 2.app
     */
    @Override
    public ResponseVo login(Customer customer) {
        //定义返回值
        ResponseVo responseVo = new ResponseVo(false,ErrorCode.FAIL,"用户账号或手机号不存在，密码错误，登陆失败");
        //校验数据非空 账号信息和密码
        if(StringUtils.isEmpty(customer.getLoginAccount())){
            //账号信息为空
            responseVo.setMsg("用户账号或手机号不能为空");
            return responseVo;
        }
        if(StringUtils.isEmpty(customer.getPassword())){
            //账号信息为空
            responseVo.setMsg("用户密码不能为空");
            return responseVo;
        }
        //密码加密
        String password = customer.getPassword();
        String s = MD5Util.inputPassToFormPass(password);
        customer.setPassword(s);

        //去数据库匹配
        Customer customerByBb = customerMapper.selectLoginCustomer(customer);
        if(customerByBb != null){
            responseVo.setMsg("登录成功");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            //返回前台用户登录信息
            responseVo.setData(customerByBb);
            //保存到redis
            redisTemplate.opsForValue().set(customerByBb.getUserAccount(),customerByBb);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 用户退出
     * @param userAccount
     * @return
     */
    @Override
    public ResponseVo loginOut(String userAccount) {
        //定义返回值
        ResponseVo responseVo = new ResponseVo(false,ErrorCode.FAIL,"退出失败！");
        //校验userAccount
        if(StringUtils.isEmpty(userAccount)){
            responseVo.setMsg("用户信息不完整，退出失败");
            return responseVo;
        }
        //有值
        //从redis清除userAccount对应的用户信息
        Boolean result = redisTemplate.delete(userAccount);
        if(result){
            responseVo.setMsg("退出成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }

        return responseVo;
    }

    /**
     * 列表查询
     * @param customer
     * @return
     */
    @Override
    public ResponseVo listCustomers(Customer customer) {
        //1.查询用户信息(未删除的信息)
        //2.模糊查询功能
        //3.封装分页信息

        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功");
        //添加分页插件
        PageHelper.startPage(customer.getPageNum(),customer.getPageSize());
        //查询用户信息(未删除的信息)
        List<Customer> customerList = customerMapper.listCustomers(customer);
        //封装分页返回信息
        PageInfo<Customer> pageInfo = new PageInfo<>(customerList);
        responseVo.setData(pageInfo);

        return responseVo;
    }

    /**
     * 用户详情查询
     * @param id
     * @return
     */
    @Override
    public ResponseVo findCustomerById(Integer id) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功");
        if(id == null){
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setMsg("用户Id不能为空");
            return responseVo;
        }
        Customer customer = customerMapper.findCustomerById(id);
        if(customer == null){
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setMsg("未查询到用户信息");
            return responseVo;
        }
        responseVo.setData(customer);
        return responseVo;
    }

    /**
     * 根据Id修改用户信息
     * @param customer
     * @return
     */
    @Override
    public ResponseVo updateCustomerById(Customer customer) {
        ResponseVo responseVo = new ResponseVo(false,ErrorCode.FAIL,"修改失败");
        Customer customerByDb = customerMapper.findCustomerByPhoneAndAccountExOwn(customer);
        if(customerByDb != null){
            //有值存在，页面输入有重复
            responseVo.setMsg("修改失败！");
            return responseVo;
        }
        BigDecimal score = new BigDecimal(customer.getFrontScore());
        customer.setScore(score);
        //对修改人进行操作
        Customer customerRedis = (Customer) redisTemplate.opsForValue().get(customer.getLoginAccount());
        if (customerRedis != null){
            customer.setUpdatedBy(customerRedis.getUserAccount());
        }else {
            customer.setUpdatedBy("admin");
        }
        int result = customerMapper.updateCustomerById(customer);
        if(result == 1){
            responseVo.setMsg("修改成功");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 根据Id删除用户信息
     * @param id
     * @return
     */
    @Override
    public ResponseVo deleteCustomerById(Integer id) {
        //字段更新is_delete
        //TODO 等完成 购物车时，添加逻辑
        ResponseVo responseVo = new ResponseVo(true,ErrorCode.SUCCESS,"删除成功！");
        if(id == null){
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setMsg("用户Id不能为空");
            return responseVo;
        }
        int result = customerMapper.deleteCustomerById(id);
        if(result != 1){
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setMsg("删除失败");
            return responseVo;
        }

        //删除和这个人相关的购物车信息
        shoppingCarMapper.deleteCarById(id);
        return responseVo;
    }

    /**
     *根据id更新密码
     * @param originPwd 原始密码
     * @param newPwd 新密码
     * @param userId 用户id
     * @param userAccount 登陆人Id
     * @return
     */
    @Override
    public ResponseVo updatePwd(String originPwd, String newPwd, Integer userId, String userAccount) {
        //校验原始密码 需要加密后去数据库匹配

        ResponseVo responseVo = new ResponseVo(false,ErrorCode.FAIL,"修改失败！");
        if(StringUtils.isEmpty(originPwd)
                || StringUtils.isEmpty(newPwd)
                || userId == null
                || StringUtils.isEmpty(userAccount)){
            responseVo.setMsg("密码或账户信息为空！");
            return responseVo;
        }


        Customer customerById = customerMapper.findCustomerById(userId);
        if (customerById == null){
            responseVo.setMsg("用户信息不存在！");
            return responseVo;
        }
        //校验原始密码
        String inputPassToFormPass = MD5Util.inputPassToFormPass(originPwd);
        String password = customerById.getPassword();
        if(!inputPassToFormPass.equals(password)){
            responseVo.setMsg("原始密码不正常！");
            return responseVo;
        }

        Map<Object,Object> map = new HashMap<>();
        map.put("newPwd",MD5Util.inputPassToFormPass(newPwd));
        map.put("userId",userId);
        //校验登录
        Customer customerRedis = (Customer) redisTemplate.opsForValue().get(userAccount);
        if (customerRedis != null){
            map.put("userAccount",userAccount);
        }else {
            map.put("userAccount","admin");
        }

        int result = customerMapper.updatePwdById(map);
        if(result == 1){
            responseVo.setMsg("密码修改成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 根据用户来源进行积分充值
     * @param frontScore 前台获取的积分
     * @param id 登入人id
     * @userAccount pc 操作人账户
     * @return
     */
    @Override
    public ResponseVo updateScore(String frontScore, Integer id, String loginAccount) {
        //1.PC端 金额大小，更新人，更新时间，充值用户id 
        //2.APP端 金额大小，充值用户id，更新人，更新时间
        ResponseVo responseVo = new ResponseVo(false,ErrorCode.FAIL,"充值失败！");
        Map<Object,Object> map = new HashMap<>();
        //通过id查询用户信息是否存在
        Customer customerById = customerMapper.findCustomerById(id);
        if(customerById == null){
            responseVo.setMsg("用户不存在");
            return responseVo;
        }
        //是否登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if(customerByRedis != null){
            //redis已经保存了登陆人信息
            map.put("userAccount",loginAccount);
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //充值用户id不为空
        if (id == null){
            responseVo.setMsg("充值Id不能为空");
            return responseVo;
        }
        //充值的金额不能为负
        if(frontScore.isEmpty() || frontScore.equals("")){
            responseVo.setMsg("充值金额不能为负！");
            return responseVo;
        }

        BigDecimal score = new BigDecimal(frontScore);
        score = score.add(customerById.getScore());
        map.put("score",score);
        map.put("userId",id);
            int result = customerMapper.updateScore(map);
            if (result == 1){
                responseVo.setMsg("充值成功！");
                responseVo.setSuccess(true);
                responseVo.setCode(ErrorCode.SUCCESS);
                return responseVo;
            }
        return responseVo;
    }


}
