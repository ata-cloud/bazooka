-- 发布配置添加机器节点字段
ALTER TABLE pms_app_deploy_config
  ADD COLUMN `cluster_nodes` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布节点' AFTER `start_command`;

-- 发布配置添加集群类型标识
ALTER TABLE pms_app_deploy_config
  ADD COLUMN `cluster_type` varchar(1) NULL COMMENT '集群类型: 0:MESOS集群 1:KUBERNETES集群 2:单节点集群' AFTER `env_id`;

-- 补充数据，发布配置的集群类型
UPDATE pms_app_deploy_config t
SET t.cluster_type = ( SELECT c.type FROM rms_env e LEFT JOIN rms_cluster c ON e.cluster_id = c.id WHERE e.id = t.env_id );

-- 拓展image_tag长度
alter table rms_docker_image modify image_tag varchar(128) not null comment '镜像tag';

-- 添加集群名称唯一索引
ALTER TABLE `rms_cluster`
ADD UNIQUE INDEX `name_only_one` (`name`) USING BTREE ;

-- 添加节点端口号
ALTER TABLE `rms_cluster_node`
ADD COLUMN `ssh_port`  varchar(10) CHARACTER SET utf8 NOT NULL DEFAULT '22' COMMENT '端口号' AFTER `create_time`;

-- 添加节点凭据id
ALTER TABLE `rms_cluster_node`
ADD COLUMN `credential_id`  int NULL COMMENT '凭据id' AFTER `used_disk`;

-- 添加集群配置凭据id
ALTER TABLE `rms_cluster_config`
ADD COLUMN `credential_id`  int NULL COMMENT '凭据id' AFTER `version`;