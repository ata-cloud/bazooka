# Mesos集群内部自建镜像库

你可以按照一下步骤创建自己的镜像库
> 请注意，需要已经创建好的DC/OS集群（Version 1.11.6）
> 如果还未完成安装，请参考[阿塔云-Bazooka 本地集群安装方案](https://github.com/ata-cloud/bazooka/blob/master/docs/install_local_cluster.md)

+ **1、创建自己的镜像文件目录**

在服务器挂载目录（此处假定为/data目录）下创建docker-registry和docker-registry-config目录

docker-registry用于存放镜像文件
docker-registry-config目录用于存放镜像库服务配置文件

+ **2、创建镜像库服务配置文件**

在docker-registry-config目录下创建以下文件config.yml，配置项可以按照您的需求修改log和storage，但一般情况下，请勿修改http和health项

```yml
version: 0.1
log:
  fields:
    service: registry
storage:
  cache:
    blobdescriptor: inmemory
  filesystem:
    rootdirectory: /var/lib/registry
  delete:
    enabled: true
http:
  addr: :5000
  headers:
    X-Content-Type-Options: [nosniff]
health:
  storagedriver:
    enabled: true
    interval: 10s
    threshold: 3
```

+ **3、复制以下marathon json并在本地DC/OS集群中创建镜像库服务**

```json
{
  "labels": {
    "HAPROXY_GROUP": "external"
  },
  "id": "/registry",
  "backoffFactor": 1.15,
  "backoffSeconds": 1,
  "container": {
    "portMappings": [
      {
        "containerPort": 5000,
        "hostPort": 0,
        "labels": {
          "VIP_0": "192.168.0.1:443"
        },
        "protocol": "tcp",
        "servicePort": 5000,
        "name": "default"
      }
    ],
    "type": "DOCKER",
    "volumes": [
      {
        "containerPath": "/var/lib/registry",
        "hostPath": "/data/docker-registry",
        "mode": "RW"
      },
      {
        "containerPath": "/etc/docker/registry/config.yml",
        "hostPath": "/data/docker-registry-config/config.yml",
        "mode": "RO"
      }
    ],
    "docker": {
      "image": "registry",
      "forcePullImage": false,
      "privileged": false,
      "parameters": []
    }
  },
  "cpus": 0.5,
  "disk": 0,
  "healthChecks": [
    {
      "gracePeriodSeconds": 60,
      "intervalSeconds": 60,
      "maxConsecutiveFailures": 3,
      "portIndex": 0,
      "timeoutSeconds": 20,
      "delaySeconds": 15,
      "protocol": "TCP",
      "ipProtocol": "IPv4"
    }
  ],
  "instances": 1,
  "maxLaunchDelaySeconds": 3600,
  "mem": 1024,
  "gpus": 0,
  "networks": [
    {
      "mode": "container/bridge"
    }
  ],
  "requirePorts": false
}
```

请注意

> 此服务将使用Marathon-LB的5000端口（由servicePort设置）已提供LB访问

> volumes中分别是第一步中创建的镜像文件保存目录挂载，和第二步中的镜像服务配置文件挂载

> cpus和mem分别是此镜像服务单个容器的cpu和内存资源限制，通常0.5 cpu、1024 MiB内存即可，也可按照实际使用情况调整

> 如果其他配置项也需要调整，请先参考[Marathon配置项](https://docs.d2iq.com/mesosphere/dcos/1.11/deploying-services/marathon-parameters/)

+ **4、验证服务**
等待一段时间服务启动之后，你可以通过以下镜像列表接口即可验证镜像库服务

```cmd
http://您的Marathon-LB的IP地址:5000/v2/_catalog?n=10000
```

您也可以在**本DC/OS集群内的服务器**上使用此镜像库服务

推送镜像

```cmd
docker push 192.168.0.1/xxx/xxx:version-number
```

拉取镜像

```cmd
docker pull 192.168.0.1/xxx/xxx:version-number
```

+ **5、配置网络以及服务器（非必须）**

当前镜像库服务只能在**本DC/OS集群内的服务器**上使用，如果需要集群外的服务器也能使用，请参考以下步骤

> 5.1、添加域名（例如xx.yy.com）指向镜像库服务，服务IP为您的Marathon-LB的IP地址，端口为5000

> 5.2、在需要使用此镜像库的服务器上，修改文件/etc/docker/daemon.json，insecure-registries中增加xx.yy.com

```json
{"insecure-registries":["xx.yy.com"]}
```
> 5.3、重启服务器的docker服务

```cmd
systemctl daemon-reload && systemctl restart docker
```

> 5.4、此服务器可以使用镜像库了，在本DC/OS集群内的服务器也可以使用相同的方式，都以域名xx.yy.com的方式使用镜像库

```cmd
docker pull xx.yy.com/xxx/xxx:version-number
```
