package com.neusoft.bookstore.shoppingcar.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.goods.mapper.GoodsMapper;
import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.shoppingcar.mapper.ShoppingCarMapper;
import com.neusoft.bookstore.shoppingcar.model.ShoppingCar;
import com.neusoft.bookstore.shoppingcar.service.ShoppingCarService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/12 9:08
 */
@Service
public class ShoppingCarServiceImpl implements ShoppingCarService {
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 添加商品到购物车
     * @param shoppingCar
     * @return
     */
    @Override
    public ResponseVo addShoppingCar(ShoppingCar shoppingCar) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "加入购物车失败！");
        /**
         * 1.登录
         * 2.商品信息(商品编码，商家编码，登陆人)
         * 3.判断从详情页加入购物车，还是购物车列表
         * 详情页第一次加入，生成一条数据，否则数量加一
         * 购物车加入：数量加一
         */
        //校验登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(shoppingCar.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            shoppingCar.setOrderUserId(customerByRedis.getId());
            shoppingCar.setCreatedBy(customerByRedis.getUserAccount());
            shoppingCar.setUpdatedBy(customerByRedis.getUserAccount());
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        if(StringUtils.isEmpty(shoppingCar.getSkuCode())
        || StringUtils.isEmpty(shoppingCar.getBusinessCode()))
        {
            responseVo.setMsg("商品信息不完整！");
            return responseVo;
        }
        //3.判断从详情页加入购物车，还是购物车列表
        ShoppingCar shoppingCarByDb = shoppingCarMapper.findGoodsFromCar(shoppingCar);
        if(shoppingCarByDb == null){
            //购物车没有记录，第一次加入，新增一条
            Goods goods = goodsMapper.findGoodsBySkuCode(shoppingCar.getSkuCode());
            if(goods.getStoreNum() <= 0){
                responseVo.setMsg("库存不足，无法添加！");
                return responseVo;
            }
            int result = shoppingCarMapper.addShoppingCar(shoppingCar);
            if(result == 1){
                responseVo.setSuccess(true);
                responseVo.setCode(ErrorCode.SUCCESS);
                responseVo.setMsg("加入购物车成功！");
                return responseVo;
            }
        }else {
            //更新一条
            Goods goods = goodsMapper.findGoodsBySkuCode(shoppingCar.getSkuCode());
            //
            if(shoppingCar.getShopNum() == null){
                //从详情页
                //当前购物车中已经有的商品数量
                Integer shopNum = shoppingCarByDb.getShopNum();
                //比较库存
                if (shopNum+1 > goods.getStoreNum()){
                    responseVo.setMsg("库存不足，无法添加！");
                    return responseVo;
                }else {
                    //满足条件
                    shoppingCar.setShopNum(shopNum+1);
                }
            }else {
                //购物车列表
                if(shoppingCar.getShopNum() > goods.getStoreNum()){
                    responseVo.setMsg("库存不足，无法添加！");
                    return responseVo;
                }
            }
        }
        int result = shoppingCarMapper.updateShoppingCar(shoppingCar);
        if(result == 1){
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setMsg("加入购物车成功！");
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo findGoodsFromCar(Integer userId, Integer pageNum, Integer pageSize) {
        /**
         * 分页
         */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "加入购物车失败！");

        if(pageNum != null && pageSize != null){
            PageHelper.startPage(pageNum,pageSize);
        }
        List<ShoppingCar> shoppingCarList = shoppingCarMapper.listGoodsFromCar(userId);
        if(shoppingCarList != null && shoppingCarList.size() > 0){
            for (int i = 0; i < shoppingCarList.size(); i++) {
                ShoppingCar car = shoppingCarList.get(i);
                List<String> goodsImages = goodsMapper.findImagesBySkuCode(car.getSkuCode());
                car.setImages(goodsImages);
            }
        }
        //返回
        if(pageNum != null && pageSize != null){
            PageInfo<ShoppingCar> shoppingCarPageInfo = new PageInfo<>(shoppingCarList);
            responseVo.setData(shoppingCarPageInfo);
        }else {
            responseVo.setData(shoppingCarList);
        }
        return responseVo;
    }

    /**
     * 删除购物车商品
     * @param shoppingCar
     * @return
     */
    @Override
    public ResponseVo deleteGoodsFromCar(ShoppingCar shoppingCar) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "删除商品失败！");
        /**、
         * 1.校验登录
         * 2.判断数据
         * 3.删除，根据商品编码，商家编码，购买人id，物理删除
         *
         */
        //校验登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(shoppingCar.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            shoppingCar.setOrderUserId(customerByRedis.getId());
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //2.判断数据
        if(StringUtils.isEmpty(shoppingCar.getSkuCode())
                || StringUtils.isEmpty(shoppingCar.getBusinessCode()))
        {
            responseVo.setMsg("商品信息不完整！");
            return responseVo;
        }
        //3.删除，根据商品编码，商家编码，购买人id，物理删除
        int result = shoppingCarMapper.deleteGoodsFromCar(shoppingCar);
        if(result == 1){
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setMsg("删除成功！");
            return responseVo;
        }
        return responseVo;
    }
}
