package com.neusoft.bookstore.goods.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.goods.mapper.GoodsMapper;
import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.goods.model.GoodsImage;
import com.neusoft.bookstore.goods.service.GoodsService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import com.neusoft.bookstore.util.StringUtil;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/7 9:32
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    // 设置好账号的ACCESS_KEY和SECRET_KEY
    @Value("${qiniu.accessKey}")
    String ACCESS_KEY;
    @Value("${qiniu.secretKey}")
    String SECRET_KEY;
    // 要上传的空间
    @Value("${qiniu.buckName}")
    String bucketname;

    // 密钥配置
    private Auth getAuth(){
        return Auth.create(ACCESS_KEY, SECRET_KEY);
    }


    // 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
    Configuration cfg = new Configuration(Zone.zone2());
    // ...其他参数参考类注释
    private UploadManager getUploadManager(){
        return new UploadManager(cfg);
    }

    // 测试域名，只有30天有效期
    @Value("${qiniu.qiniuDomin}")
    private String QINIU_IMAGE_DOMAIN;

    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return getAuth().uploadToken(bucketname);
    }
    /**
     * 查询全部商家
     * @return
     */
    @Override
    public ResponseVo listBusiness() {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        List<Goods> mapList = goodsMapper.listBusiness();
        responseVo.setData(mapList);
        return responseVo;
    }

    /**
     * 图片上传
     * @param file
     * @return
     */
    @Override
    public ResponseVo uploadImage(MultipartFile file) {
        /**
         * 1.校验file是否存在
         * 2.校验图片格式 jpg,png,gif,psd
         * 3.文件上传
         * a:图片格式不变，命名
         * b:七牛云认证参数信息
         */
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "上传失败！");
        if(file == null){
            responseVo.setMsg("图片文件不存在");
            return responseVo;
        }
        //校验图片格式 jpg,png,gif,psd
        //获取原始文件名称
        String originalFilename = file.getOriginalFilename();
        //获取后缀名
        String type = FilenameUtils.getExtension(originalFilename);
        if(StringUtils.isEmpty(type)){
            responseVo.setMsg("图片类型为空，请检查后重试！");
            return responseVo;
        }
        if("JPG".equals(type.toUpperCase()) || "PNG".equals(type.toUpperCase())
        || "GIF".equals(type.toUpperCase()) || "PSD".equals(type.toUpperCase())){
            //格式正确
            String fileName = System.currentTimeMillis()+"."+type;
            //七牛云认证参数信息
            try {
                Response response = getUploadManager().put(file.getBytes(),fileName,getUpToken());
                if(response.isOK() && response.isJson()){
                    //获取图片在七牛云的地址
                    Object key = JSONObject.parseObject(response.bodyString()).get("key");
                    responseVo.setData(QINIU_IMAGE_DOMAIN+key);
                    responseVo.setMsg("图片上传成功");
                    responseVo.setSuccess(true);
                    responseVo.setCode(ErrorCode.SUCCESS);
                    return responseVo;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            responseVo.setMsg("请上传【jpg,png,gif,psd】类型的图片！");
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 商品新增
     * @param goods
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo addGoods(Goods goods) {
        /**
         * 1.商品重复校验
         * 2.售价定价格式转换
         * 3.多张图片，集合接收，地址保存到数据库
         * 4.商品和图片在2张表 先增商品再增图片（事务）sku_code关联
         */

        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "新增失败！");
        if(goods.getLoginAccount() == null){
            responseVo.setMsg("请传入指定参数！");
            return responseVo;
        }
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(goods.getLoginAccount());
        String createBy = "";
        if(customerByRedis != null){
            //redis已保存
            createBy = goods.getLoginAccount();
            goods.setCreatedBy(createBy);
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //1.商品重复校验
        Goods goodsByCondition = goodsMapper.findGoodsByCondition(goods);
        if(goodsByCondition != null){
            responseVo.setMsg("该商品已存在，请勿重复添加！");
            return responseVo;
        }
        //2.售价定价格式转换
        if(!StringUtils.isEmpty(goods.getFrontCostPrice())){
            //处理定价
            BigDecimal decimal = new BigDecimal(goods.getFrontCostPrice());
            goods.setCostPrice(decimal);
        }
        if(!StringUtils.isEmpty(goods.getFrontSalePrice())){
            //处理售价
            BigDecimal decimal = new BigDecimal(goods.getFrontSalePrice());
            goods.setSalePrice(decimal);
        }
        //新增商品 先生成sku_code
        String skuCode = StringUtil.getCommonCode(2);
        goods.setSkuCode(skuCode);
        int result = goodsMapper.addGoods(goods);
        if(result != 1){
            responseVo.setMsg("新增失败！");
            return responseVo;
        }
        //图片处理
        List<String> images = goods.getImages();
        if(images != null && images.size() > 0){
            //新增商品图片
            for (int i = 0; i < images.size(); i++) {
                GoodsImage goodsImage = new GoodsImage();
                goodsImage.setSkuCode(skuCode);
                goodsImage.setSkuImagesPath(images.get(i));
                goodsImage.setCreatedBy(createBy);
                int result1 = goodsMapper.addImages(goodsImage);
                if(result1 != 1){
                    responseVo.setMsg("新增失败！");
                    return responseVo;
                }
            }
        }

        responseVo.setCode(ErrorCode.SUCCESS);
        responseVo.setSuccess(true);
        responseVo.setMsg("新增成功！");
        return responseVo;
    }

    /**
     * 查询全部商品
     * @param goods
     * @return
     */
    @Override
    public ResponseVo listGoods(Goods goods) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //添加分页信息
        PageHelper.startPage(goods.getPageNum(),goods.getPageSize());
        List<Goods> goodsList = goodsMapper.listGoods(goods);
        if(goodsList != null && goodsList.size() > 0){
            //查询图片
            for (int i = 0; i < goodsList.size(); i++) {
                //根据skuCode查询图片
                Goods goodByDb = goodsList.get(i);
                List<String> goodImages = goodsMapper.findImagesBySkuCode(goodByDb.getSkuCode());
                goodByDb.setImages(goodImages);
            }
        }
        //返回
        PageInfo<Goods> pageInfo = new PageInfo<>(goodsList);
        responseVo.setData(pageInfo);
        return responseVo;
    }

    /**
     * 商品详情查询
     * @param skuCode
     * @return
     */
    @Override
    public ResponseVo findGoodsBySkuCode(String skuCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        /**
         * 1.校验skuCode是否存在
         * 2.根据skuCode查询商品详情
         * 3.根据skuCode查询商品图片
         * 4.将图片和商品一起返回
         *
         */
        //1.校验skuCode是否存在
        if(StringUtils.isEmpty(skuCode)){
            responseVo.setMsg("商品编码不能为空");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        //2.根据skuCode查询商品详情(根据商品编码，查询商家名称)
       Goods goods = goodsMapper.findGoodsBySkuCode(skuCode);
        if(goods == null){
            responseVo.setMsg("未查询到指定商品");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        //3.根据skuCode查询商品图片
        List<String> goodImages = goodsMapper.findImagesBySkuCode(skuCode);
        goods.setImages(goodImages);
        //4.将图片和商品一起返回
        responseVo.setData(goods);
        return responseVo;
    }

    /**
     * 根据商家编码查询商家详情
     * @param businessCode
     * @return
     */
    @Override
    public ResponseVo findBusinessByCode(String businessCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        /**
         * 1.校验businessCode是否存在
         * 2.查询 返回Map 无实体类
         */
        if(StringUtils.isEmpty(businessCode)){
            responseVo.setMsg("商家编码不能为空");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        //2.查询 返回Map 无实体类
        Map<String,Object> map = goodsMapper.findBusinessByCode(businessCode);
        if(map == null){
            responseVo.setMsg("未查询到指定商家信息");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        //返回
        responseVo.setData(map);
        return responseVo;
    }

    /**
     * 根据商品编码修改商品信息
     * @param goods
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo updateGoodsInfo(Goods goods) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败！");

        /**
         * 1.校验登录
         * 2.商品重复校验
         * 3.售价定价格式转换
         * 4.多张图片，集合接收，地址保存到数据库
         * 5.商品和图片在2张表 先增商品再增图片（事务）sku_code关联
         *
         *
         */
        //1.校验登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(goods.getLoginAccount());
        String createBy = "";
        if(customerByRedis != null){
            //redis已保存
            createBy = goods.getLoginAccount();
            goods.setUpdatedBy(createBy);
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //2.商品重复校验(排除自己)
        Goods goodsByCondition = goodsMapper.findGoodsByCondition(goods);
        if(goodsByCondition != null){
            responseVo.setMsg("该商品已存在，请勿重复添加！");
            return responseVo;
        }
        //3.售价定价格式转换
        if(!StringUtils.isEmpty(goods.getFrontCostPrice())){
            //处理定价
            BigDecimal decimal = new BigDecimal(goods.getFrontCostPrice());
            goods.setCostPrice(decimal);
        }
        if(!StringUtils.isEmpty(goods.getFrontSalePrice())){
            //处理售价
            BigDecimal decimal = new BigDecimal(goods.getFrontSalePrice());
            goods.setSalePrice(decimal);
        }
        //修改商品信息
        int result = goodsMapper.updateGoodsInfo(goods);
        if(result != 1){
            responseVo.setMsg("修改失败！");
            return responseVo;
        }
        //多张图片，集合接收，地址保存到数据库
        //先删除原来的
        goodsMapper.deleteImages(goods.getSkuCode());
        //新增商品图片
        List<String> images = goods.getImages();
        if(images != null && images.size() > 0){
            //新增商品图片
            for (int i = 0; i < images.size(); i++) {
                GoodsImage goodsImage = new GoodsImage();
                goodsImage.setSkuCode(goods.getSkuCode());
                goodsImage.setSkuImagesPath(images.get(i));
                goodsImage.setCreatedBy(createBy);
                int result1 = goodsMapper.addImages(goodsImage);
                if(result1 != 1){
                    responseVo.setMsg("新增失败！");
                    return responseVo;
                }
            }
        }
        responseVo.setMsg("修改成功！");
        responseVo.setSuccess(true);
        responseVo.setCode(ErrorCode.SUCCESS);
        return responseVo;
    }

    /**
     * 根据商品编码删除商品信息
     * @param skuCode
     * @param loginAccount
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo deleteGoods(String skuCode, String loginAccount) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "删除失败！");
        /**
         * 1.校验登录
         * 2.校验skuCode是否存在
         * 3.根据skuCode删除商品信息，然后skuCode删除商品图片
         */
        //1.校验登录
        if(StringUtils.isEmpty(loginAccount) || StringUtils.isEmpty(skuCode)){
            responseVo.setMsg("信息不完整！");
            return responseVo;
        }
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if(customerByRedis == null){
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //3.根据skuCode删除商品信息 物理删除
        int result = goodsMapper.deleteGoods(skuCode);
        if(result != 1){
            return responseVo;
        }
        //4.skuCode删除商品图片
        goodsMapper.deleteImages(skuCode);
        responseVo.setMsg("删除成功！");
        responseVo.setSuccess(true);
        responseVo.setCode(ErrorCode.SUCCESS);
        return responseVo;
    }

    /**
     * 商品的上架和下架
     * @param skuCode
     * @param loginAccount
     * @param status
     * @return
     */
    @Override
    public ResponseVo updateGoodStatus(String skuCode, String loginAccount, String status) {
        /**
         * 1.校验登录
         * 2.校验skuCode,loginAccount status(0,1)
         */

        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败！");

        if(StringUtils.isEmpty(loginAccount) || StringUtils.isEmpty(skuCode)
        || StringUtils.isEmpty(status)){
            responseVo.setMsg("信息不完整！");
            return responseVo;
        }
        //登录
        String updatedBy = "";
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if(customerByRedis == null){
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }else {
            updatedBy = customerByRedis.getUserAccount();
        }
        if(!"0".equals(status) && !"1".equals(status)){
            responseVo.setMsg("商品状态不正确！");
            return responseVo;
        }
        int result = goodsMapper.updateGoodStatus(skuCode,updatedBy,status);
        if(result == 1){
            responseVo.setMsg("修改成功！");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            return responseVo;
        }
        return responseVo;
    }
}
