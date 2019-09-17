# 项目管理

pms 项目异常 code 前缀:  0010108+xxxx


## 2.0

### 功能

1. 重构发布
2. jar 发布
3. 实时 log
### sql 变动

ops-pms/mysql/version/2/ops_pms.sql

### 配置变动

```properties

deploy.cluster.TEST.computerRoom = GS
deploy.cluster.TEST.jenkins.url = http://10.0.33.11/service/jenkins/
deploy.cluster.TEST.jenkins.username = ops-deploy
deploy.cluster.TEST.jenkins.password = ops-deploy
deploy.cluster.TEST.env = test
deploy.cluster.TEST.dockerHub.url = http://registry.gs.youyuwo.com
deploy.cluster.TEST.marathon.endpoint = http://10.0.33.11/service/marathon/
deploy.cluster.TEST.marathon.optionListen = true
deploy.cluster.TEST.marathon.mlb-endpoint = http://10.0.33.15:9090/

deploy.cluster.PRE.computerRoom = GS
deploy.cluster.PRE.jenkins.url = http://10.0.33.11/service/jenkins/
deploy.cluster.PRE.jenkins.username = ops-deploy
deploy.cluster.PRE.jenkins.password = ops-deploy
deploy.cluster.PRE.env = PRE
deploy.cluster.PRE.dockerHub.url = http://registry.gs.youyuwo.com
deploy.cluster.PRE.marathon.endpoint = http://10.0.33.11/service/marathon/
deploy.cluster.PRE.marathon.optionListen = true
deploy.cluster.PRE.marathon.mlb-endpoint = http://10.0.33.15:9090/

deploy.cluster.JR.computerRoom = GS
deploy.cluster.JR.jenkins.url = http://10.0.33.11/service/jenkins/
deploy.cluster.JR.jenkins.username = ops-deploy
deploy.cluster.JR.jenkins.password = ops-deploy
deploy.cluster.JR.env = PROD
deploy.cluster.JR.dockerHub.url = http://registry.gs.youyuwo.com
deploy.cluster.JR.marathon.endpoint = http://10.0.33.11/service/marathon/
deploy.cluster.JR.marathon.optionListen = true
deploy.cluster.JR.marathon.mlb-endpoint = http://10.0.33.15:9090/


deploy.cluster.JAR.computerRoom = GS
deploy.cluster.JAR.jenkins.url = http://10.0.33.11/service/jenkins/
deploy.cluster.JAR.jenkins.username = ops-deploy
deploy.cluster.JAR.jenkins.password = ops-deploy
deploy.cluster.JAR.env = JAR

deploy.callback.jenkinsRetry = 10
deploy.callback.marathonRetry = 10


```

### 定时job变动

```text
* 任务管理->ops pms系统(执行器选择)->新增任务
* 执行器: ops pms系统
* 任务描述: 清理 docker registry
* cron: 0 0 3 1/1 * ?
* JobHandler: dockerRegistryCleanJob
* 负责人: 文石良
* 报警邮件: wenshiliang@yofish.com
 
* 任务管理->ops pms系统(执行器选择)->新增任务
* 执行器: ops pms系统
* 任务描述: 回调deploy 返回 marathon 部署结果
* cron: 0 0/1 *  * * ?
* JobHandler: deployMarathonResultCallbackRetryJob
* 负责人: 文石良
* 报警邮件: wenshiliang@yofish.com
  
* 任务管理->ops pms系统(执行器选择)->新增任务
* 执行器: ops pms系统
* 任务描述: 回调deploy 返回 jenkins 部署结果
* cron: 0 0/1 *  * * ?
* JobHandler: deployJenkinsResultCallbackRetryJob
* 负责人: 文石良
* 报警邮件: wenshiliang@yofish.com

```