-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
    `password` varchar(32) DEFAULT NULL COMMENT '密码',
    `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `phone_idx` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- 用户信息表
DROP TABLE IF EXISTS user_info;
CREATE TABLE `user_info` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `user_id` bigint(20) NOT NULL COMMENT '用户ID',
     `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
     `avatar` varchar(100) DEFAULT NULL COMMENT '头像',
     `tags` varchar(50) DEFAULT NULL COMMENT '标签,逗号隔开',
     `sex` tinyint(1) DEFAULT '2' COMMENT '性别,0-男，1-女,2-未知',
     `age` int(11) DEFAULT NULL COMMENT '用户年龄',
     `edu` tinyint(1) DEFAULT '9' NULL COMMENT '学历,0-文盲,1-小学,2-初中,3-中专,4-高中,5-大专,6-本科,7-硕士,8-博士,9-未知',
     `school` varchar(20) DEFAULT NULL COMMENT '学校',
     `city` varchar(20) DEFAULT NULL COMMENT '城市',
     `birthday` varchar(20) DEFAULT NULL COMMENT '生日',
     `industry` varchar(20) DEFAULT NULL COMMENT '行业',
     `income` varchar(20) DEFAULT NULL COMMENT '收入',
     `status` tinyint(1) DEFAULT '3' NULL COMMENT '状态,0-单身,1-恋爱中,2-已婚,3-未知',
     `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `user_id_idx` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';