# 电商项目
主要技术：ssm框架的整合使用、maven进行依赖管理、实现前后端分离，返回json格式数据
系统框架：All in one，tomcat服务器，ftp服务器，nginx服务器配置在一台主机上
- 用户模块（数据响应对象ServerResponse封装、mybatis-generator自动生成表、用户密码md5+salt值加密、guava本地缓存通过重置密码的时forget_token）
- 商品类别模块（递归算法遍历商品类别及这个商品类别下的所有子类别）
- 商品模块（Spring-mvc文件上传、商品图片上传到ftp服务器，通过nginx的反向代理访问ftp服务器的商品图片、mybatis-pagehelper的动态排序以及分页）
- 购物车模块（Bigdemical的string构造器解决浮点数丢失精度问题）
- 收货地址模块
- 支付模块（与支付宝当面付功能的对接，支付宝预下单成功后的生成二维码后上传到ftp服务器、支付宝回调函数的编写）
- 订单模块 (与支付模块的整合、随机订单号的生成)