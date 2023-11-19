# 数据库初始化
# @author <a href="https://github.com/jarvischan666">JarvisChan</a>
# @from <a href="https://jvc.icu">编程导航知识星球</a>
# 用户表
create table tag
(
    id          bigint auto_increment comment 'id' primary key ,
    tagName     varchar(256)                       null comment '标签名称',
    userId   bigint                      null comment '用户 id',
    parentId bigint                     null comment '父标签 id',
    isParent   tinyint                   not null comment '是否是父标签',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '标签';


# [加入编程导航](https://t.zsxq.com/0emozsIJh) 入门捷径+交流答疑+项目实战+求职指导，帮你自学编程不走弯路
