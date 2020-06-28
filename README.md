## A back-end development project imitating Jingdong Walking Bookstore

### 项目概述 (💬 pause update)

👍*一个基于SSM的书店系统 : 代码注释详细,逻辑结构清晰。*

🔑*数据库中默认的管理员身份信息 : 账户名 : `admin` , 密码 `123`*

### 开发环境

| 工具    | 版本或描述           |
| ------- | -------------------- |
| `OS`    | Windows 10           |
| `JDK`   | 1.8                  |
| `IDE`   | IntelliJ IDEA 2020.1 |
| `Maven` | 3.3.9                |
| `MySQL` | 5.7.27               |

> 本项目的数据库版本为`5.7.27`，请广大同学注意咯：可通过逐个复制表结构来创建该数据库哟 ~

### 项目结构

```
├─database file
│      xzsd.sql
│
│
└─sms
    │  pom.xml
    │
    │
	└─src
	   └─main
	   │  └─java
	   │  │  └─com
	   │  │      └─neusoft
	   │  │          └─bookstore
	   │  │              │  BookStoreApplication.java
	   │  │              │  
	   │  │              ├─cate
	   │  │              │  ├─controller
	   │  │              │  │      CateController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      CateMapper.java
	   │  │              │  │      
	   │  │              │  ├─model
	   │  │              │  │      Cate.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  CateService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              CateServiceImpl.java
	   │  │              │              
	   │  │              ├─customer
	   │  │              │  ├─controller
	   │  │              │  │      CustomerController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      CustomerMapper.java
	   │  │              │  │      
	   │  │              │  ├─model
	   │  │              │  │      Customer.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  CustomerService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              CustomerServiceImpl.java
	   │  │              │              
	   │  │              ├─demo
	   │  │              │  ├─controller
	   │  │              │  │      DemoController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      DemoMapper.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  DemoService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              DemoServiceImpl.java
	   │  │              │              
	   │  │              ├─filter
	   │  │              │      SimpleCORSFilter.java
	   │  │              │      
	   │  │              ├─goods
	   │  │              │  ├─controller
	   │  │              │  │      GoodsController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      GoodsMapper.java
	   │  │              │  │      
	   │  │              │  ├─model
	   │  │              │  │      Goods.java
	   │  │              │  │      GoodsImage.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  GoodsService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              GoodsServiceImpl.java
	   │  │              │              
	   │  │              ├─menu
	   │  │              │  ├─controller
	   │  │              │  │      MenuController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      MenuMapper.java
	   │  │              │  │      
	   │  │              │  ├─model
	   │  │              │  │      Menu.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  MenuService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              MenuServiceImpl.java
	   │  │              │              
	   │  │              ├─order
	   │  │              │  ├─controller
	   │  │              │  │      OrderController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      OrderMapper.java
	   │  │              │  │      
	   │  │              │  ├─model
	   │  │              │  │      Order.java
	   │  │              │  │      OrderDetail.java
	   │  │              │  │      OrderVo.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  OrderService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              OrderServiceImpl.java
	   │  │              │              
	   │  │              ├─picture
	   │  │              │  ├─controller
	   │  │              │  │      PictureController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      PictureMapper.java
	   │  │              │  │      
	   │  │              │  ├─model
	   │  │              │  │      Picture.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  PictureService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              PictureServiceImpl.java
	   │  │              │              
	   │  │              ├─shoppingcar
	   │  │              │  ├─controller
	   │  │              │  │      ShoppingCarController.java
	   │  │              │  │      
	   │  │              │  ├─mapper
	   │  │              │  │      ShoppingCarMapper.java
	   │  │              │  │      
	   │  │              │  ├─model
	   │  │              │  │      ShoppingCar.java
	   │  │              │  │      
	   │  │              │  └─service
	   │  │              │      │  ShoppingCarService.java
	   │  │              │      │  
	   │  │              │      └─impl
	   │  │              │              ShoppingCarServiceImpl.java
	   │  │              │              
	   │  │              └─util
	   │  │                      BaseModel.java
	   │  │                      BaseTree.java
	   │  │                      ErrorCode.java
	   │  │                      GlobalExceptionHandler.java
	   │  │                      GoodsInfoException.java
	   │  │                      MD5Util.java
	   │  │                      MethodUtils.java
	   │  │                      ResponseVo.java
	   │  │                      StringUtil.java
	   │  │                      
	   │  └─resources
	   │      │  application.yml
	   │      │  
	   │      └─mapper
	   │          ├─cate
	   │          │      CateMapper.xml
	   │          │      
	   │          ├─customer
	   │          │      CustomerMapper.xml
	   │          │      
	   │          ├─demo
	   │          │      DemoMapper.xml
	   │          │      
	   │          ├─goods
	   │          │      GoodsMapper.xml
	   │          │      
	   │          ├─menu
	   │          │      MenuMapper.xml
	   │          │      
	   │          ├─order
	   │          │      OrderMapper.xml
	   │          │      
	   │          ├─picture
	   │          │      PictureMapper.xml
	   │          │      
	   │          └─shoppingcar
	   │                  ShoopingCarMapper.xml
	   │                  
	   └─test
	       └─java
	           └─com
	               └─neusoft
	                   └─bookstore
	                           BookStoreApplicationTests.java
```

#### 项目文件说明-`数据库文件`

```
xzsd.sql
```

#### 项目文件说明-`数据库,七牛云，Redis及Spring核心配置信息`

```
application.yml
```

#### 项目文件说明-`Mapper 接口映射文件`
```
mapper/
```
