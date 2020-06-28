package com.neusoft.bookstore.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.mapper.CustomerMapper;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.goods.mapper.GoodsMapper;
import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.order.mapper.OrderMapper;
import com.neusoft.bookstore.order.model.Order;
import com.neusoft.bookstore.order.model.OrderDetail;
import com.neusoft.bookstore.order.model.OrderVo;
import com.neusoft.bookstore.order.service.OrderService;
import com.neusoft.bookstore.shoppingcar.mapper.ShoppingCarMapper;
import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.GoodsInfoException;
import com.neusoft.bookstore.util.ResponseVo;
import com.neusoft.bookstore.util.StringUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/13 10:47
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;


    /**
     * 订单创建
     *
     * @param orderVos
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo addOrder(List<OrderVo> orderVos) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "创建失败！");

        //1.获取登陆人
        if (orderVos == null && orderVos.size() <= 0) {
            responseVo.setMsg("未购买任何商品！");
            return responseVo;
        }
        String loginAccount = orderVos.get(0).getLoginAccount();
        Integer userId = null;
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(orderVos.get(0).getLoginAccount());
        if (customerByRedis != null) {
            //redis已保存
            userId = customerByRedis.getId();
        } else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //2.先找出有多少个商家，找到该商家下的商品
/*        public static void main(String[] args) {

            OrderVo vo = new OrderVo("SKU1","BUSSIIN1",30,"YINS");
            OrderVo vo1 = new OrderVo("SKU2","BUSSIIN1",40,"YINS");
            OrderVo vo2 = new OrderVo("SKU3","BUSSIIN2",30,"YINS");
            OrderVo vo3 = new OrderVo("SKU4","BUSSIIN3",30,"YINS");

            List<OrderVo> orderVos = Arrays.asList(vo, vo1, vo2, vo3);

            HashSet hashSet = new HashSet();
            for (int i = 0; i < orderVos.size(); i++) {
                hashSet.add(orderVos.get(i).getBusinessCode());
            }
            Iterator iterator = hashSet.iterator();
            Map<String,List<OrderVo>> hashMap = new HashMap();   // 存放 商家和对应的商品集合  key：商家code，value :商家对应的商品集合

            while (iterator.hasNext()){
                String next = (String) iterator.next(); // 商家code
                List<OrderVo> list = new ArrayList<OrderVo>();//商家对应的商品集合
                for (int i = 0; i < orderVos.size(); i++) {
                    String businessCode = orderVos.get(i).getBusinessCode();
                    if(next.equals(businessCode)){
                        //
                        list.add( orderVos.get(i));
                    }
                }
                hashMap.put(next,list);
            }

            System.out.println(hashMap);

        }*/
        //先取出用户余额
        Customer customerById = customerMapper.findCustomerById(userId);
        BigDecimal score = customerById.getScore();
        //定义总价格 所有订单的总价格
        Map<String, List<OrderVo>> hasMap =
                orderVos.stream().collect(Collectors.groupingBy(OrderVo::getBusinessCode));
        //遍历 1.先找出有多少个商家，找到该商家下 有多少个商品
        //定义总价格 所有订单的总价格
        BigDecimal orderAmountAll = new BigDecimal(0.0);
        //2.存放 商家和对应的商品集合 key:商家code value:商家对应的商品集合
        for (Map.Entry<String, List<OrderVo>> entry : hasMap.entrySet()) {
            //取出商家编码
            String businessCode = entry.getKey();
            //购买的商品集合
            List<OrderVo> voList = entry.getValue();
            //计算订单总金额
            BigDecimal orderAmount = new BigDecimal(0.0);

            //生成订单编码
            String orderCode = StringUtil.getCommonCode(2);

            //处理订单详情
            for (int i = 0; i < voList.size(); i++) {
                //商品状态，商品库存，用户余额
                OrderVo orderVo = voList.get(i);
                //校验商品状态 该商品是否已经下架
                Goods goodsBySkuCode = goodsMapper.findGoodsBySkuCode(orderVo.getSkuCode());
                if (goodsBySkuCode == null || goodsBySkuCode.getSkuStatus() != 0) {

                    throw new GoodsInfoException("商品已经下架，无法购买！");

                }
                if (orderVo.getShopNum() > goodsBySkuCode.getStoreNum()) {
                    throw new GoodsInfoException("用户余额不足，无法购买！");
                }
                //用户余额
                //计算每种商品的总价格
                BigDecimal skuAmount = goodsBySkuCode.getSalePrice().multiply(new BigDecimal(orderVo.getShopNum()));
                //将每种商品总价格加入到订单总价
                orderAmount.add(skuAmount);
                //将订单总价加入到总价中
                orderAmountAll = orderAmountAll.add(orderAmount);


                /**
                 * 比较用户余额和总价
                 */
                if (orderAmountAll.compareTo(score) == 1) {
                    throw new GoodsInfoException("用户余额不足，无法购买！");

                }
                //创建订单详情信息

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderCode(orderCode);
                orderDetail.setShopNum(orderVo.getShopNum());
                orderDetail.setSkuCode(orderVo.getSkuCode());
                orderDetail.setSkuAmount(skuAmount);
                orderDetail.setCreatedBy(loginAccount);
                orderMapper.addOrderDetail(orderDetail);

                //需要减少商品库存，增加商品的销售量
                Map<String, Object> map = new HashMap<>();
                map.put("skuCode", orderVo.getSkuCode());
                map.put("shopNum", orderVo.getShopNum());
                goodsMapper.updateGoodsStoreAndSaNum(map);

                //删除购物车
                ShoppingCar shoppingCar = new ShoppingCar();
                shoppingCar.setSkuCode(orderVo.getSkuCode());
                shoppingCar.setBusinessCode(orderVo.getBusinessCode());
                shoppingCar.setOrderUserId(userId);
                shoppingCarMapper.deleteGoodsFromCar(shoppingCar);

            }
            //订单表生成
            Order order = new Order();
            order.setBusinessCode(businessCode);
            order.setOrderUserId(userId);
            order.setOrderAmount(orderAmount);
            order.setOrderStatus(0);
            order.setPayStatus(1);
            order.setOrderCode(orderCode);
            orderMapper.addOrder(order);
        }
        //减少用户余额
        String updateScore = score.subtract(orderAmountAll).toString();
        HashMap map = new HashMap();
        map.put("score", updateScore);
        map.put("userId", userId);
        customerMapper.updateScore(map);
        responseVo.setMsg("创建成功！");
        responseVo.setSuccess(true);
        responseVo.setCode(ErrorCode.SUCCESS);
        return responseVo;
    }

    /**
     * 订单查询
     * @param
     * @return
     */
    @Override
    public ResponseVo listOrders(Order order) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
       /* Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(order.getLoginAccount());
        if (customerByRedis == null) {
            //redis已保存
            responseVo.setMsg("请登录后重试！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }*/
        /**
         * 1.不传参数，查询所有
         * 2.通过orderCode,phone,username
         */
        //查询全部订单信息 进行分页
        //添加分页信息
        PageHelper.startPage(order.getPageNum(),order.getPageSize());
        List<Order> orderList = orderMapper.listOrders(order);
        if(orderList == null && orderList.size() == 0){
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            responseVo.setMsg("查询的数据为空！");
            return responseVo;
        }
        PageInfo<Order> orderPageInfo = new PageInfo<>(orderList);
        responseVo.setData(orderPageInfo);

        return responseVo;
    }

    /**
     * 订单详情查询
     * @param orderCode
     * @return
     */
    @Override
    public ResponseVo findOrderByOrderCode(String orderCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        Order order = orderMapper.findOrderByOrderCode(orderCode);
        responseVo.setData(order);
        if(order == null){
            responseVo.setMsg("查询失败！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * App订单记录查询
     * @param loginAccount
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public ResponseVo findAllOrderForApp(String loginAccount, Integer pageSize, Integer pageNum) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if (customerByRedis == null) {
            responseVo.setMsg("请登录后重试！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //查询该用户所有的订单遗迹订单下的商品信息
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.findAllOrderForApp(loginAccount);
        if(orderList == null && orderList.size() == 0){
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            responseVo.setMsg("查询的数据为空！");
            return responseVo;
        }
        PageInfo<Order> orderPageInfo = new PageInfo<>(orderList);
        responseVo.setData(orderPageInfo);
        return responseVo;
    }
}
