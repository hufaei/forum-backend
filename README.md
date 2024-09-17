# 校园论坛后端
### 框架简介:<br>

- **Sa-Token**: 一个轻量级 Java 权限认证框架，支持登录认证、权限认证、Session 会话、单点登录等功能。  
  [Sa-Token](https://sa-token.dev33.cn/)

- **Spring Boot + MyBatis-Plus**: Spring Boot 是用于简化 Spring 应用开发的框架，MyBatis-Plus 是增强版的 MyBatis ORM 框架，提供了 CRUD 操作的增强支持。  
  [了解更多](https://baomidou.com/)

- **Redisson + Redis**: Redisson 是一个 Redis 客户端工具库，支持分布式锁、集合等高级数据结构操作，Redis 是一个基于内存的键值存储数据库。  
  [Redisson](https://redisson.org/)  
  [Redis 官网](https://redis.io/)

- **GoEasy + RabbitMQ**: GoEasy 是一个实时消息推送服务，适合即时通讯和实时通知，RabbitMQ 是一个流行的消息队列中间件，支持异步消息处理和分布式系统间通信。  
  [GoEasy](https://www.goeasy.io/)  
  [RabbitMQ 官网](https://www.rabbitmq.com/)

**前端项目源码地址**: [forum--template](https://github.com/hufaei/forum--template)
---
* _项目开发规范详解见：_ [项目开发规范](https://github.com/hufaei/forum--template/docx "开发规范")
    * [前端开发规范](docx/前端命名规范.txt)
    * [后端开发规范](docx/后端开发规范.txt)
    * [Api命名规范](docx/api命名规范.txt)

### 项目结构：

```
├─src                              // 源代码目录
│  ├─main                          // 主程序代码
│  │  ├─java                       // Java 源代码目录
│  │  │  └─com
│  │  │      └─lisan
│  │  │          └─forumbackend    // 项目根包
│  │  │              ├─common      // 通用功能模块（工具类、常量等）
│  │  │              ├─config      // 配置类模块（如 Spring 配置、数据库配置）
│  │  │              ├─controller  // 控制器模块（处理 HTTP 请求的控制层）
│  │  │              ├─exception   // 异常处理模块（自定义异常及其处理）
│  │  │              ├─mapper      // 数据访问层（MyBatisPlus映射——仅继承baseMapper）
│  │  │              ├─model       // 数据模型
│  │  │              │  ├─dto      // 数据传输对象（DTO：用于传输数据的对象，以下均包含：增删查改）
│  │  │              │  │  ├─announcements // 公告相关 DTO
│  │  │              │  │  ├─comments      // 评论相关 DTO
│  │  │              │  │  ├─follows       // 关注相关 DTO
│  │  │              │  │  ├─replies       // 回复相关 DTO
│  │  │              │  │  ├─sections      // 板块相关 DTO
│  │  │              │  │  ├─topics        // 主题相关 DTO
│  │  │              │  │  └─users         // 用户相关 DTO
│  │  │              │  ├─entity           // 实体类（与数据库表映射）
│  │  │              │  ├─enums            // 枚举类（定义常量类型：图床数据和权限）
│  │  │              │  └─vo               // 视图对象（VO：用于向前端展示的数据对象）
│  │  │              ├─service             // 服务层接口
│  │  │              │  └─impl             // 服务层实现类
│  │  │              └─utils               // 工具类（常用的功能或辅助方法）
│  │  └─resources                          // 资源文件（配置文件、静态资源等）
│  │      └─static                         // 静态资源（无）
│  └─test                                  // 测试代码目录
│      └─java                              // Java 测试代码
│          └─com
│              └─example
│                  └─forumbackend          
└─target(无关紧要)                        // 编译输出目录（通常忽略或不提交到版本控制）
```
### 使用须知：
- ***下载包后先修改yml文件下一系列配置（端口，配置，账密），包括数据库创建，maven插件下载***</br>
- ***手动controller层的MqListener对goeasy对应appKey的修改（自行申请），以及Mq的队列交换机手动创建***</br>
- ***使用图床：本项目中使用了[图仓](https://tucang.cc/#/login?redirect=/home/dashboard),其api暴露较少，若需更多功能（删除图片等等也不是不能用但是需要自行解析网址使用的api，我尝试使用但是感觉较为繁琐，所以功能只有上传）-- 也可自行修改图床以及其相关代码***</br>
***<h6>此文件只针对后端的部署，前端的部署使用详见其README.md文件</h6>***
### 关于项目：

### 致谢

本项目参考并使用了以下开源仓库的功能：

- [MybatisPlus模板代码生成器](https://github.com/gnanquanmama/tropical-fish "tropical-fish") 。
