CREATE TABLE upms_user
(
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键',
    username VARCHAR(255) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(255) COMMENT '真实姓名',
    sex TINYINT(1) COMMENT '性别(0:男 1:女)',
    phone VARCHAR(255) COMMENT '手机号',
    email VARCHAR(255) COMMENT '邮箱',
    remark VARCHAR(255) COMMENT '备注',
    status VARCHAR(8) NOT NULL COMMENT '状态(0:有效 1:无效)',
    create_author VARCHAR(255) COMMENT '创建者',
    update_author VARCHAR(255) COMMENT '更新者',
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE TABLE upms_role
(
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键',
    role_name VARCHAR(255) NOT NULL COMMENT '角色名称',
    remark VARCHAR(255) COMMENT '备注',
    create_author VARCHAR(255) COMMENT '创建者',
    update_author VARCHAR(255) COMMENT '更新者',
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE TABLE upms_permission
(
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键',
    code VARCHAR(8) NOT NULL COMMENT '权限编码(默认sys)',
    permission_name VARCHAR(255) NOT NULL COMMENT '权限名称',
    url VARCHAR(255) NOT NULL COMMENT '权限url',
    type TINYINT(4) NOT NULL COMMENT '权限类型(0:菜单 1:按钮 2:iframe 3:其他)',
    icon VARCHAR(255) COMMENT '图标',
    remark VARCHAR(255) COMMENT '备注',
    parent_id BIGINT(20) NOT NULL COMMENT '父级id(0代表根权限)',
    rank TINYINT(4) COMMENT '排序',
    iframe_url VARCHAR(255) COMMENT 'iframe地址',
    iframe_json VARCHAR(2000) COMMENT 'iframe数据',
    create_author VARCHAR(255) COMMENT '创建者',
    update_author VARCHAR(255) COMMENT '更新者',
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE TABLE upms_role_permission
(
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键',
    role_id BIGINT(20) NOT NULL COMMENT '角色id',
    permission_id BIGINT(20) NOT NULL COMMENT '权限id',
    create_author VARCHAR(255) COMMENT '创建者',
    update_author VARCHAR(255) COMMENT '更新者',
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE TABLE upms_user_role
(
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键',
    user_id BIGINT(20) NOT NULL COMMENT '用户id',
    role_id BIGINT(20) NOT NULL COMMENT '角色id',
    create_author VARCHAR(255) COMMENT '创建者',
    update_author VARCHAR(255) COMMENT '更新者',
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX idx_user_role_name ON upms_role (role_name);
CREATE UNIQUE INDEX idx_role_id_permission_id ON upms_role_permission (role_id, permission_id);
CREATE UNIQUE INDEX idx_user_username ON upms_user (username);
CREATE UNIQUE INDEX idx_user_id_role_id ON upms_user_role (user_id, role_id);
