package com.neusoft.bookstore.cate.service.impl;

import com.neusoft.bookstore.cate.mapper.CateMapper;
import com.neusoft.bookstore.cate.model.Cate;
import com.neusoft.bookstore.cate.service.CateService;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.menu.model.Menu;
import com.neusoft.bookstore.util.BaseTree;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author joy
 * @version 1.0
 * @date 2020/4/30 9:46
 */
@Service
public class CateServiceImpl implements CateService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private CateMapper cateMapper;
    /**
     * 新增分类
     * @param cate
     * @return
     */
    @Override
    public ResponseVo addCate(Cate cate) {

        /**
         * 1.规定了 一级分类的父级分类为0
         * 2.同一层分类名不重复
         * 3.当某一级有子类时，需要变更当前级别为父分类
         * 4.获取登陆人
         * 5.新增
         */

        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "新增失败！");
        //1.获取登陆人
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(cate.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            cate.setCreatedBy(cate.getLoginAccount());
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //1.规定了 一级分类的父级分类为0
        //获取前台传入的分类编码
        String frontCateCode = cate.getFrontCateCode();
        //若前台传入的分类编码为空，就设置父级分类编码为0
        if(StringUtils.isEmpty(frontCateCode)){
            cate.setParentCateCode("0");
        }else {
            //创建二级菜单设置父级分类菜单编码为前台传入的分类编码
            cate.setParentCateCode(frontCateCode);
        }
        //2.同一层分类名不重复
        Cate cateByDb = cateMapper.findCateByParentCateCodeAndName(cate);
        if(cateByDb != null){
            //已经存在同名分类
            responseVo.setMsg("当前级别分类名重复");
            return responseVo;
        }
        //不存在相同分类
        //3.当某一级有子类时，需要变更当前级别为父分类
        //新增
        int result = cateMapper.insertCate(cate);
        if(result == 1){
            responseVo.setMsg("新增成功");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 分类信息修改
     * @param cate
     * @return
     */
    @Override
    public ResponseVo updateCateByCode(Cate cate) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败！");
        //1.获取登陆人
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(cate.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            cate.setUpdatedBy(cate.getLoginAccount());
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
/*        //获取前台传入的分类编码
        String frontCateCode = cate.getFrontCateCode();
        //若前台传入的分类编码为空，就设置父级分类编码为0
        if(StringUtils.isEmpty(frontCateCode)){
            cate.setParentCateCode("0");
        }else {
            //创建二级菜单设置父级分类菜单编码为前台传入的分类编码
            cate.setParentCateCode(frontCateCode);
        }*/
        //2.同一层分类名不重复
        Cate cateByDb = cateMapper.findCateByParentCateCodeAndName(cate);
        if(cateByDb != null){
            //已经存在同名分类
            responseVo.setMsg("当前级别分类名重复");
            return responseVo;
        }
        //修改
        int result = cateMapper.updateCateByCode(cate);
        if(result == 1){
            responseVo.setMsg("修改成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 分类树查询
     * @return
     */
    @Override
    public ResponseVo listCateTree() {
        /**
         * 1.找出所有的分类
         * 2.将分类信息封装成有层级关系的树的信息
         * 3.树返回
         */
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //1.找出所有的分类
        List<Cate> cateList = cateMapper.listCates();
        if(cateList.size() == 0 || cateList == null){
            responseVo.setMsg("未查询到任何菜单信息！");
            return responseVo;
        }
        //2.将分类信息封装成有层级关系的树的信息
        BaseTree rootTree = new BaseTree();
        //创建根节点
        String rootNodeId = "0";
        initTree(rootTree,cateList,rootNodeId);
        //返回数据
        responseVo.setData(rootTree.getChildNodes());
        return responseVo;
    }

    /**
     * 分类树详情查询
     * @param cateCode
     * @return
     */
    @Override
    public ResponseVo findCateByCode(String cateCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
            //数据校验
        if(StringUtils.isEmpty(cateCode)){
            responseVo.setMsg("菜单编码不能为空！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //根据菜单编码查询菜单详情
        Cate cate = cateMapper.findCateByCode(cateCode);
        if(cate == null){
            responseVo.setMsg("菜单为空！");
            return responseVo;
        }
        responseVo.setData(cate);
        return responseVo;
    }

    /**
     * 删除分类信息
     * @param cateCode
     * @return
     */
    @Override
    public ResponseVo deleteCateByCode(String cateCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "删除成功！");
        //数据校验
        if(StringUtils.isEmpty(cateCode)){
            responseVo.setMsg("分类编码不能为空！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        List<Cate> cateList = cateMapper.findChildCates(cateCode);
        if(cateList != null && cateList.size() != 0){
            responseVo.setMsg("当前菜单下有子级，无法删除！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        int resultNum = cateMapper.findGoodsByCateCode(cateCode);
        if(resultNum != 0){
            //证明该分类下 一定存在某些商品
            responseVo.setMsg("该分类下有商品，无法删除！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        int resultRow = cateMapper.deleteCateByCode(cateCode);
        if (resultRow == 0){
            responseVo.setMsg("删除失败！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 级联加载商品
     * @param cateCode
     * @return
     */
    @Override
    public ResponseVo findCateByCateCode(String cateCode) {
        //级联:既要加载一级分类，又要根据一级分类加载二级分类
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        String parentCateCode = null;
        if(StringUtils.isEmpty(cateCode)){
            //加载一级分类
            parentCateCode = "0";
        }else {
            parentCateCode = cateCode;
        }
        List<Cate> childCates = cateMapper.findChildCates(parentCateCode);
        responseVo.setData(childCates);
        return responseVo;
    }

    private void initTree(BaseTree rootTree, List<Cate> cateList, String rootNodeId) {
        //遍历树
        Iterator<Cate> iterator = cateList.iterator();
        while (iterator.hasNext()){
            Cate cate = iterator.next();
            //先创建根树
            if(cate.getCateCode().equals(rootNodeId)){
                cateToTree(rootTree,cate);
            }else if (cate.getParentCateCode().equals(rootNodeId)){
                BaseTree childTree = new BaseTree();
                cateToTree(childTree,cate);
                //需要将子树加到根树
                //需判断根节点的子节点是否创建
                if(childTree.getNodeId() != null){
                    //需判断根节点的子节点是否创建
                    if(rootTree.getChildNodes() == null){
                        //初始化根树的子节点
                        ArrayList<BaseTree> list = new ArrayList<>();
                        //先初始化空的，装进根树
                        rootTree.setChildNodes(list);
                    }
                    //需要将子树加到根树
                    rootTree.getChildNodes().add(childTree);
                }
                //递归处理
                initTree(childTree,cateList,cate.getCateCode());
            }

        }

    }

    private void cateToTree(BaseTree rootTree, Cate cate) {
        //节点的id存菜单编码
        rootTree.setNodeId(cate.getCateCode());
        rootTree.setNodeName(cate.getCateName());
        rootTree.setAttribute(cate);
    }
}
