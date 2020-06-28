package com.neusoft.bookstore.picture.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.picture.mapper.PictureMapper;
import com.neusoft.bookstore.picture.model.Picture;
import com.neusoft.bookstore.picture.service.PictureService;
import com.neusoft.bookstore.util.BaseTree;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/5/11 09:20
 */
@Service
public class PictureServiceImpl implements PictureService {
        @Autowired
        private PictureMapper pictureMapper;
        @Autowired
        private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 轮播图新增
     * @param picture
     * @return
     */
    @Override
    public ResponseVo addPic(Picture picture) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "新增成功！");

        //校验登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(picture.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            picture.setCreatedBy(picture.getLoginAccount());
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        //新增
        int result = pictureMapper.addPic(picture);
        if(result != 1){
            responseVo.setMsg("新增失败！");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * app+pc
     * @param picture
     * @return
     */
    @Override
    public ResponseVo listPic(Picture picture) {
        /**
         * 1.PC的轮播图查询带分页，模糊查询条件(图片状态)
         * 2.APP 不带分页，图片状态（启用）
         *
         */
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //判断分页
        if(picture.getPageNum() != null && picture.getPageSize() != null){
            //pc端查询，带分页 封装分页信息
            PageHelper.startPage(picture.getPageNum(),picture.getPageSize());
        }
        //直接查询
        List<Picture> pictureList = pictureMapper.listPic(picture);
        //返回
        if(picture.getPageNum() != null && picture.getPageSize() != null){
            //pc端查询，带分页 返回分页信息
            PageInfo<Picture> picturePageInfo = new PageInfo<>(pictureList);
            responseVo.setData(picturePageInfo);
        }else {
            //返回app 不带分页
            responseVo.setData(pictureList);
        }
        return responseVo;
    }

    /**
     * 轮播图状态更新包含删除
     * @param picture
     * @return
     */
    @Override
    public ResponseVo updatePic(Picture picture) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "操作失败！");
        /**
         * 1.删除(id)
         * 2.启用禁用(id+status)
         */
        //校验登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(picture.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            picture.setUpdatedBy(picture.getLoginAccount());
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //校验状态
        Integer status = picture.getPicStatus();
        if(status != null && status != 1 && status !=2){
            //传值有问题
            responseVo.setMsg("轮播图状态不正确！");
            return responseVo;
        }
        int result = pictureMapper.updatePic(picture);
        if(result ==1){
            responseVo.setMsg("操作成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;
    }
}
