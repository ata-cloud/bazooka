ALTER TABLE `ata_ops`.`pms_app_deploy_config`
  ADD COLUMN `cluster_nodes` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布节点' AFTER `start_command`;

ALTER TABLE `ata_ops`.`pms_app_deploy_config`
  ADD COLUMN `cluster_type` varchar(1) NULL COMMENT '集群类型: 0:MESOS集群 1:KUBERNETES集群 2:单节点集群' AFTER `env_id`;