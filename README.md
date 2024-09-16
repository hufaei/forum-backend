# 校园论坛后端
satoken鉴权 + springboot + mybatis-plus +redis
<h4>todo - mq集成</h4>
<h4>todo - redis分布式锁设计，存储设计使用</h4>
<h4>前端地址：https://github.com/hufaei/forum--template</h4>
项目结构：

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


