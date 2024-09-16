# 校园论坛后端
satoken鉴权 + springboot + mybatis-plus +redis
<h4>todo - mq集成</h4>
<h4>todo - redis分布式锁设计，存储设计使用</h4>
<h4>前端地址：https://github.com/hufaei/forum--template</h4>
项目结构：
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─lisan
│  │  │          └─forumbackend
│  │  │              ├─common
│  │  │              ├─config
│  │  │              ├─controller
│  │  │              ├─exception
│  │  │              ├─mapper
│  │  │              ├─model
│  │  │              │  ├─dto
│  │  │              │  │  ├─announcements
│  │  │              │  │  ├─comments
│  │  │              │  │  ├─follows
│  │  │              │  │  ├─replies
│  │  │              │  │  ├─sections
│  │  │              │  │  ├─topics
│  │  │              │  │  └─users
│  │  │              │  ├─entity
│  │  │              │  ├─enums
│  │  │              │  └─vo
│  │  │              ├─service
│  │  │              │  └─impl
│  │  │              └─utils
│  │  └─resources
│  │      └─static
│  └─test
│      └─java
│          └─com
│              └─example
│                  └─forumbackend
└─target
    ├─classes
    │  ├─com
    │  │  └─lisan
    │  │      └─forumbackend
    │  │          ├─common
    │  │          ├─config
    │  │          ├─controller
    │  │          ├─exception
    │  │          ├─mapper
    │  │          ├─model
    │  │          │  ├─dto
    │  │          │  │  ├─announcements
    │  │          │  │  ├─comments
    │  │          │  │  ├─follows
    │  │          │  │  ├─replies
    │  │          │  │  ├─sections
    │  │          │  │  ├─topics
    │  │          │  │  └─users
    │  │          │  ├─entity
    │  │          │  ├─enums
    │  │          │  └─vo
    │  │          ├─service
    │  │          │  └─impl
    │  │          └─utils
    │  └─static
    ├─generated-sources
    │  └─annotations
    ├─generated-test-sources
    │  └─test-annotations
    └─test-classes
        └─com
            └─example
                └─forumbackend
