ALTER TABLE `ata_ops`.`pms_app_deploy_config`
ADD COLUMN `cluster_nodes` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布节点' AFTER `start_command`;