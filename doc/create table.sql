CREATE TABLE `LEAVE` (
  `leave_id` int(11) NOT NULL AUTO_INCREMENT,
  `process_instance_id` varchar(45) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `leave_type` varchar(45) DEFAULT NULL,
  `reason` varchar(100) DEFAULT NULL,
  `apply_time` timestamp NULL DEFAULT NULL,
  `reality_start_time` timestamp NULL DEFAULT NULL,
  `reality_end_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`leave_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='请假表';


CREATE TABLE `USER` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(16) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  `locked` tinyint(4) NOT NULL DEFAULT '0',
  `salt` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `ROLE` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE `PERMISSION` (
  `perm_id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `type` varchar(45) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`perm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资源';


CREATE TABLE `USER_ROLE` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色的关系表';

CREATE TABLE `ROLE_PERMISSION` (
  `role_id` int(11) NOT NULL,
  `perm_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和资源的关系表';