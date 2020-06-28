package com.neusoft.bookstore.menu.service.impl;

import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.menu.mapper.MenuMapper;
import com.neusoft.bookstore.menu.model.Menu;
import com.neusoft.bookstore.menu.service.MenuService;
import com.neusoft.bookstore.util.BaseTree;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
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
 * @date 2020/4/27 11:21
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    /**
     * 新增菜单
     * @param menu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo addMenu(Menu menu) {

        /**
         * 1.规定了 一级菜单的父级菜单为0
         * 2.同一层菜单名不重复
         * 3.当某一级有子菜单时，需要变更当前级别为目录
         * 4.获取登陆人
         * 5.新增
         */

        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "新增失败！");
        //4.获取登陆人
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(menu.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            menu.setCreatedBy(menu.getLoginAccount());
        }else {
           //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //1.规定了 一级菜单的父级菜单为0,子级的父级菜单编码是父级菜单的菜单编码
        String frontMenuCode = menu.getFrontMenuCode();
        if(StringUtils.isEmpty(frontMenuCode)){
            //若为空，则创建一级菜单
            menu.setParentMenuCode("0");
        }else {
            //二级菜单
            menu.setParentMenuCode(frontMenuCode);
        }
        //2.同一层菜单名不重复
        Menu menuByDb = menuMapper.findMenuByParentMenuCodeAndName(menu);
        if(menuByDb != null){
            //已经存在同名菜单
            responseVo.setMsg("当前级别菜单名重复");
            return responseVo;
        }
        //3.当某一级有子菜单时，需要变更当前级别为目录
        if(!"0".equals(menu.getParentMenuCode())){
            //更新操作
            //查询 父级菜单类型
            Menu menuByMenuCode = menuMapper.findMenuByMenuCode(menu.getParentMenuCode());
            if(menuByMenuCode != null){
                if(menuByMenuCode.getMenuType() ==2){
                    //更新 修改为目录 清空menu_url
                    menuMapper.updateTypeUrlByCode(menu.getParentMenuCode());
                }
            }
        }
        //新增
        int result = menuMapper.insertMenu(menu);
        if(result == 1){
            responseVo.setMsg("新增成功");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;

    }

    /**
     * 菜单树查询
     * @return
     */
    @Override
    public ResponseVo listMenuTree() {
        /**
         * 1.找出所有的菜单
         * 2.将菜单信息封装成有层级关系的树的信息
         * 3.树返回
         */

        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //1.找出所有的菜单
        List<Menu> menuList = menuMapper.listMenus();
        if(menuList == null || menuList.size() ==0){
            responseVo.setMsg("未查询到任何菜单信息！");
            return responseVo;
        }
        //2.将菜单信息封装成有层级关系的树的信息
         //先创建一棵树
        BaseTree rootTree = new BaseTree();
        //根节点树的id为0
        String rootNodeId = "0";
        //初始化树
        initTree(rootTree,menuList,rootNodeId);
        //树返回
        responseVo.setData(rootTree.getChildNodes());
        return responseVo;
    }


    /**
     * 查询菜单详情
     * @param menuCode
     * @return
     */
    @Override
    public ResponseVo findMenuByCode(String menuCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //数据校验
        if(StringUtils.isEmpty(menuCode)){
            responseVo.setMsg("菜单编码不能为空！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //根据菜单编码查询菜单详情
        Menu menu = menuMapper.findMenuByMenuCode(menuCode);
        if(menu == null){
            responseVo.setMsg("菜单为空！");
            return responseVo;

        }
        responseVo.setData(menu);
        return responseVo;
    }

    /**
     * 修改菜单信息
     * @param menu
     * @return
     */
    @Override
    public ResponseVo updateMenuByCode(Menu menu) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败！");
        //先判断是否登录
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(menu.getLoginAccount());
        if(customerByRedis != null){
            //redis已保存
            menu.setCreatedBy(menu.getLoginAccount());
        }else {
            //提示需要登录
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        Menu menuBydb = menuMapper.findMenuByParentMenuCodeAndName(menu);
        if(menuBydb != null){
            //证明当前目录下有重复
            responseVo.setMsg("当前级别下菜单名重复");
            return responseVo;
        }
        //更新操作
        int result = menuMapper.updateMenuByCode(menu);
        if(result == 1){
            responseVo.setMsg("修改成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;
    }

    /**
     * 删除菜单
     * @param menuCode
     * @return
     */
    @Override
    public ResponseVo deleteMenuByCode(String menuCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "删除成功！");
        //数据校验
        if(StringUtils.isEmpty(menuCode)){
            responseVo.setMsg("菜单编码不能为空！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        List<Menu> menuList = menuMapper.findChildMenus(menuCode);
        if(menuList != null && menuList.size() > 0){
            responseVo.setMsg("当前菜单下有子级，无法删除！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        int result = menuMapper.deleteMenuByCode(menuCode);
        if (result != 1){
            responseVo.setMsg("删除失败！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        return responseVo;
    }


    private void initTree(BaseTree rootTree, List<Menu> menuList, String rootNodeId) {
        //遍历menuList 给里面每一个菜单找位置
        Iterator<Menu> iterator = menuList.iterator();
        while (iterator.hasNext()){
            /**
             * 找位置
             * 判断menu的menuCode和rootNodeId
             * 1.层级相同
             * 2.是一颗新树的开始
             * parentMenuCode和rootNodeId
             */
            Menu menu = iterator.next();
            //先创建根树
            if(menu.getMenuCode().equals(rootNodeId)){
                menuToTree(rootTree,menu);
            }else if (menu.getParentMenuCode().equals(rootNodeId)){
                BaseTree childTree = new BaseTree();
                menuToTree(childTree,menu);
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
                initTree(childTree,menuList,menu.getMenuCode());
            }
        }
    }

    private void menuToTree(BaseTree rootTree, Menu menu) {
        //节点的id存菜单编码
        rootTree.setNodeId(menu.getMenuCode());
        rootTree.setNodeName(menu.getMenuName());
        rootTree.setNodeUrl(menu.getMenuUrl());
        rootTree.setAttribute(menu);
    }


}
