create table rms_cluster
(
	id bigint auto_increment comment '主键' primary key,
	name varchar(255) not null comment '集群名称',
	room_type varchar(255) not null comment '机房类型',
	status varchar(8) not null comment '集群状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)',
	type varchar(8) not null comment '集群类型 0:DC/OS',
	version varchar(64) not null comment '集群版本信息',
	cpu decimal(19,2) null comment 'cpu信息',
	memory decimal(19,2) null comment '内存信息',
	disk decimal(19,2) null comment '磁盘信息',
	create_author varchar(255) null comment '创建者',
	update_author varchar(255) null comment '更新者',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
);

create table rms_cluster_app
(
	id bigint auto_increment comment '主键' primary key,
	cluster_id bigint null comment '集群外键',
	app_id varchar(255) null comment 'app id',
	image varchar(255) null comment '镜像',
	cpu decimal(19,2) null comment 'cpu信息',
	disk decimal(19,2) null comment '磁盘信息',
	memory decimal(19,2) null comment '内存信息',
	instances int(8) null comment '实例数',
	version varchar(255) null comment '版本',
	tasks_staged int(8) null comment 'staged的数量',
	tasks_running int(8) null comment '运行的数量',
	tasks_healthy int(8) null comment '通过健康检查数量',
	tasks_unhealthy int(8) null comment '未通过健康检查数量',
	active tinyint(1) null comment '当前激活版本',
	app_json json null comment 'app json信息',
	create_author varchar(255) null comment '创建者',
	update_author varchar(255) null comment '更新者',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
	constraint idx_unq_app_id_version
		unique (app_id, version)
);

create table rms_cluster_config
(
	id bigint auto_increment comment '主键' primary key,
	cluster_id bigint null comment '集群外键',
	type varchar(8) not null comment '集群类型: 0:集群master 1:集群mlb 3:镜像库',
	url varchar(255) not null comment 'url地址',
	status varchar(8) not null comment '状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)',
	create_author varchar(255) null comment '创建者',
	update_author varchar(255) null comment '更新者',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
);

create table rms_cluster_node
(
	id bigint auto_increment comment '主键' primary key,
	cluster_id bigint null comment '集群外键',
	node_id varchar(255) null comment '节点id',
	ip varchar(255) null comment 'ip地址',
	node_type varchar(64) null comment '节点类型',
	status varchar(8) null comment '健康状态: 0:健康(绿色) 1:异常(红色)',
	container_quantity int(8) null comment '容器数量',
	cpu decimal(19,2) null comment '总cpu信息',
	memory decimal(19,2) null comment '总内存信息',
	disk decimal(19,2) null comment '总磁盘信息',
	used_cpu decimal(19,2) null comment '已使用cpu信息',
	used_memory decimal(19,2) null comment '已使用内存信息',
	used_disk decimal(19,2) null comment '已使用磁盘信息',
	create_author varchar(255) null comment '创建者',
	update_author varchar(255) null comment '更新者',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
);

create table rms_cluster_node_config
(
	id bigint auto_increment comment '主键' primary key,
	cluster_node_id bigint null comment '集群节点外键',
	cmd varchar(255) null comment 'cmd命令',
	create_author varchar(255) null comment '创建者',
	update_author varchar(255) null comment '更新者',
	update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
);

