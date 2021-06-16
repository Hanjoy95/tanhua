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
     `user_id` bigint(20) NOT NULL COMMENT '用户id',
     `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
     `avatar` varchar(100) DEFAULT NULL COMMENT '用户头像',
     `tags` varchar(50) DEFAULT NULL COMMENT '用户标签：多个用逗号分隔',
     `sex` tinyint(1) DEFAULT '2' COMMENT '性别，0-男，1-女，2-未知',
     `age` int(11) DEFAULT NULL COMMENT '用户年龄',
     `edu` varchar(20) DEFAULT NULL COMMENT '学历',
     `city` varchar(20) DEFAULT NULL COMMENT '居住城市',
     `birthday` varchar(20) DEFAULT NULL COMMENT '生日',
     `cover_pic` varchar(50) DEFAULT NULL COMMENT '封面图片',
     `industry` varchar(20) DEFAULT NULL COMMENT '行业',
     `income` varchar(20) DEFAULT NULL COMMENT '收入',
     `marriage` varchar(20) DEFAULT NULL COMMENT '婚姻状态',
     `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `user_id_idx` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';