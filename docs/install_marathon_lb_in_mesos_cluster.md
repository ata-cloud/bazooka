# 安装Marathon-LB服务

如果您的Marahon-LB服务暂未安装，请参考此文档

> 请注意，需要已经创建好的DC/OS集群（Version 1.11.6）

> 如果还未完成安装，请参考[阿塔云-Bazooka 本地集群安装方案](https://github.com/ata-cloud/bazooka/blob/master/docs/install_local_cluster.md)

请复制以下marathon json，并在Marathon页面（地址为DC/OS master节点IP:8080）创建marathon-lb服务

如果需要调整部分参数，请参考json下方的部分参数解释

```json
{
  "id": "/marathon-lb",
  "cmd": null,
  "cpus": 3,
  "mem": 3072,
  "disk": 0,
  "instances": 2,
  "acceptedResourceRoles": [
    "slave_public"
  ],
  "container": {
    "type": "DOCKER",
    "docker": {
      "forcePullImage": false,
      "image": "mesosphere/marathon-lb:v1.12.2",
      "parameters": [
        {
          "key": "label",
          "value": "created_by=marathon"
        },
        {
          "key": "label",
          "value": "dcos_pkg_name=marathon-lb"
        }
      ],
      "privileged": true
    },
    "volumes": [
      {
        "containerPath": "/dev/log",
        "hostPath": "/dev/log",
        "mode": "RW"
      }
    ]
  },
  "env": {
    "HAPROXY_GLOBAL_DEFAULT_OPTIONS": "redispatch,http-server-close,dontlognull,httplog",
    "HAPROXY_SSL_CERT": "",
    "HAPROXY_SYSCTL_PARAMS": "net.ipv4.tcp_tw_reuse=1 net.ipv4.tcp_fin_timeout=30 net.ipv4.tcp_max_syn_backlog=10240 net.ipv4.tcp_max_tw_buckets=400000 net.ipv4.tcp_max_orphans=60000 net.core.somaxconn=10000"
  },
  "healthChecks": [
    {
      "gracePeriodSeconds": 60,
      "intervalSeconds": 5,
      "maxConsecutiveFailures": 2,
      "path": "/_haproxy_health_check",
      "portIndex": 2,
      "protocol": "MESOS_HTTP",
      "ipProtocol": "IPv4",
      "timeoutSeconds": 2,
      "delaySeconds": 15
    }
  ],
  "labels": {
    "DCOS_PACKAGE_OPTIONS": "eyJtYXJhdGhvbi1sYiI6eyJhdXRvLWFzc2lnbi1zZXJ2aWNlLXBvcnRzIjpmYWxzZSwiYmluZC1odHRwLWh0dHBzIjp0cnVlLCJjcHVzIjozLCJoYXByb3h5X2dsb2JhbF9kZWZhdWx0X29wdGlvbnMiOiJyZWRpc3BhdGNoLGh0dHAtc2VydmVyLWNsb3NlLGRvbnRsb2dudWxsIiwiaGFwcm94eS1ncm91cCI6ImV4dGVybmFsIiwiaGFwcm94eS1tYXAiOnRydWUsImluc3RhbmNlcyI6MiwibWVtIjoyMDQ4LCJtaW5pbXVtSGVhbHRoQ2FwYWNpdHkiOjAuNSwibWF4aW11bU92ZXJDYXBhY2l0eSI6MC4yLCJuYW1lIjoibWFyYXRob24tbGIiLCJwYXJhbWV0ZXJzIjpbXSwicm9sZSI6InNsYXZlX3B1YmxpYyIsInN0cmljdC1tb2RlIjpmYWxzZSwic3lzY3RsLXBhcmFtcyI6Im5ldC5pcHY0LnRjcF90d19yZXVzZT0xIG5ldC5pcHY0LnRjcF9maW5fdGltZW91dD0zMCBuZXQuaXB2NC50Y3BfbWF4X3N5bl9iYWNrbG9nPTEwMjQwIG5ldC5pcHY0LnRjcF9tYXhfdHdfYnVja2V0cz00MDAwMDAgbmV0LmlwdjQudGNwX21heF9vcnBoYW5zPTYwMDAwIG5ldC5jb3JlLnNvbWF4Y29ubj0xMDAwMCIsImNvbnRhaW5lci1zeXNsb2dkIjpmYWxzZSwibWF4LXJlbG9hZC1yZXRyaWVzIjoxMCwicmVsb2FkLWludGVydmFsIjoxMCwidGVtcGxhdGUtdXJsIjoiIiwibWFyYXRob24tdXJpIjoiaHR0cDovL21hcmF0aG9uLm1lc29zOjgwODAiLCJzZWNyZXRfbmFtZSI6IiJ9fQ==",
    "DCOS_PACKAGE_SOURCE": "https://universe.mesosphere.com/repo",
    "DCOS_PACKAGE_METADATA": "eyJwYWNrYWdpbmdWZXJzaW9uIjoiNC4wIiwibmFtZSI6Im1hcmF0aG9uLWxiIiwidmVyc2lvbiI6IjEuMTIuMiIsIm1haW50YWluZXIiOiJzdXBwb3J0QG1lc29zcGhlcmUuaW8iLCJkZXNjcmlwdGlvbiI6IkhBUHJveHkgY29uZmlndXJlZCB1c2luZyBNYXJhdGhvbiBzdGF0ZSIsInRhZ3MiOlsibG9hZGJhbGFuY2VyIiwic2VydmljZS1kaXNjb3ZlcnkiLCJyZXZlcnNlLXByb3h5IiwicHJveHkiLCJoYXByb3h5Il0sInNlbGVjdGVkIjpmYWxzZSwic2NtIjoiaHR0cHM6Ly9naXRodWIuY29tL21lc29zcGhlcmUvbWFyYXRob24tbGIiLCJmcmFtZXdvcmsiOmZhbHNlLCJwcmVJbnN0YWxsTm90ZXMiOiJXZSByZWNvbW1lbmQgYXQgbGVhc3QgMiBDUFVzIGFuZCAxR2lCIG9mIFJBTSBmb3IgZWFjaCBNYXJhdGhvbi1MQiBpbnN0YW5jZS4gXG5cbipOT1RFKjogRm9yIGFkZGl0aW9uYWwgYGBgRW50ZXJwcmlzZSBFZGl0aW9uYGBgIERDL09TIGluc3RydWN0aW9ucywgc2VlIGh0dHBzOi8vZG9jcy5tZXNvc3BoZXJlLmNvbS9hZG1pbmlzdHJhdGlvbi9pZC1hbmQtYWNjZXNzLW1ndC9zZXJ2aWNlLWF1dGgvbWxiLWF1dGgvIiwicG9zdEluc3RhbGxOb3RlcyI6Ik1hcmF0aG9uLWxiIERDL09TIFNlcnZpY2UgaGFzIGJlZW4gc3VjY2Vzc2Z1bGx5IGluc3RhbGxlZCFcblNlZSBodHRwczovL2dpdGh1Yi5jb20vbWVzb3NwaGVyZS9tYXJhdGhvbi1sYiBmb3IgZG9jdW1lbnRhdGlvbi4iLCJwb3N0VW5pbnN0YWxsTm90ZXMiOiJNYXJhdGhvbi1sYiBEQy9PUyBTZXJ2aWNlIGhhcyBiZWVuIHVuaW5zdGFsbGVkIGFuZCB3aWxsIG5vIGxvbmdlciBydW4uIiwibGljZW5zZXMiOlt7Im5hbWUiOiJBcGFjaGUgTGljZW5zZSBWZXJzaW9uIDIuMCIsInVybCI6Imh0dHBzOi8vZ2l0aHViLmNvbS9tZXNvc3BoZXJlL21hcmF0aG9uL2Jsb2IvbWFzdGVyL0xJQ0VOU0UifSx7Im5hbWUiOiJHTlUgR2VuZXJhbCBQdWJsaWMgTGljZW5zZSB2ZXJzaW9uIDIiLCJ1cmwiOiJodHRwOi8vd3d3LmhhcHJveHkub3JnL2Rvd25sb2FkLzEuNi9kb2MvTElDRU5TRSJ9XSwiaW1hZ2VzIjp7Imljb24tc21hbGwiOiJodHRwczovL2Rvd25sb2Fkcy5tZXNvc3BoZXJlLmNvbS9hc3NldHMvdW5pdmVyc2UvMDAwL21hcmF0aG9uLWxiLWljb24tc21hbGwucG5nIiwiaWNvbi1tZWRpdW0iOiJodHRwczovL2Rvd25sb2Fkcy5tZXNvc3BoZXJlLmNvbS9hc3NldHMvdW5pdmVyc2UvMDAwL21hcmF0aG9uLWxiLWljb24tbWVkaXVtLnBuZyIsImljb24tbGFyZ2UiOiJodHRwczovL2Rvd25sb2Fkcy5tZXNvc3BoZXJlLmNvbS9hc3NldHMvdW5pdmVyc2UvMDAwL21hcmF0aG9uLWxiLWljb24tbGFyZ2UucG5nIn19",
    "DCOS_PACKAGE_DEFINITION": "eyJtZXRhZGF0YSI6eyJDb250ZW50LVR5cGUiOiJhcHBsaWNhdGlvbi92bmQuZGNvcy51bml2ZXJzZS5wYWNrYWdlK2pzb247Y2hhcnNldD11dGYtODt2ZXJzaW9uPXY0IiwiQ29udGVudC1FbmNvZGluZyI6Imd6aXAifSwiZGF0YSI6Ikg0c0lBQUFBQUFBQUFLMWFlVy9peUxiL0tyNjhmKzY3Q3NRMnlVd1RxYVVYZGhnZ1lWOW1SaGt2QlhZb0w5Y3VBNmJWMy8yZEt0dkVoNldYMFVSS0F1VlRaL3VkemFyNlV2QTFZNnR0YkhjekkwRm9lMjdocWZCUWtndDNCVmR6Q0h4eHRFQmpsdWNXcVE2THV4T1JVbExVa2dwTEFhRkVDOGxwZS9uVEhXeXlYUWEvSkFES01QSjlMMkQvNTVEUUMzMkxCS1JrZTdEUkpLRVIyRDVMK0xXZlh3UHZFRXVHNTY3dFRSUVFVNHBDVUV2cXB3cElJZE1ZZ1gxTTI0U0ZwOThMMU5OTVhhT2FhNENZdTBKSWdwMXRrS0pwaDRZSGVzWkNONjR3S2ZxY05YelAvbHRhOHVsUHZvMFNneEd6OE9SR2xNSjN3d0Z0TE1iODhPbitmbU16SzlKTGh1ZmNmNmgvajMyeUozcG9nMllwZzNVQWp0dDd3VFpiOEFQU2NVRjVTZ2NlSTZCNllVNmtnQUJQaDdpbXBER0pPNUJKcWxSN25ZYVNCbXRLeTY1SzNsb2FQZmVsdFJkSVJET3NreU9LdmFwa2M0WmdlRW42dy8zRC9jL2daZEw0ejVQVUJGTE5ORzN1VW8xS2YvMzFWOE5sSlBBRE95UlNJMW1IUmFsZXUzOFpDeDVCWlBERjhFNEtDWkV5cTAzUENFczV1TGo1bXVuWXJnMDdOTDdoM2phTG9HaFJNd3dTaGtWbncrNHovMnNScys0ZHFpY2Z1Tk85a0owNW9QL2h3RlNaY2JKYnNyUlEwZ2tCdENQQmVnMHVqQk56S1NYbXYvNXd4emxGdnd1UDhCNllFNEdybWRDOGxHbzBkZTJmMWlseVQ0b0ltUFkycFpMclNkUnpOeVNRZ2tod3A3REhEVG5QMzc5a1dmUU1hV1lScVpjOGt0SmtrVlNSYVZGQWZ6ams3blhxNmZBdEJGenZlNTFhWXpCdUZMN2VuUVMxQmxPcFJTRHhBUC9YU0FkZFRrSjNtVkFrRWlUdTkvdFNtaEVsTDlnQS9IdVhKOWU5VXZxRng4S0hITWdYaUlLNjRZV2pzNnlIZWxBcDNKMnFSZUhwUzJHblB2dCtQd0tIZ2VrVDR2aVU1KzlUQVpKajA2bDEvVld0OHd2OC81V29pbVU0VFZsclZhTGUvR0QzbkVkTG44OHE2M0Vuck5uUG00NURJNk04c25SblFJMFk5clRaci9xOEdTOFhJMDlYSDVqZTZrVGEvUEZvdHByUlVwMGQxd3M1M1RmWW00dSsvV0pYcjhqZ3p3WWdZLzlicC9ac2d6eldlZmMycE16WWN0RzF6SmExMDIwbFhOcVBiRFZYY25STlgzZEhGc2lqaGkzMFA1cUw2bDR2ZCtWaFM2R0dXamthTFlzYXpoUjArbVQzbm9XdDc3cjZLQy9uTkZvdE9rSWZvUi93TTl0MHZ4b0xQbzJKUE9pTlpwMTBEOWQvdEZ1cUxKSFREbjhUYTJLZHN1WGNwRnpmanN0K05aelpVUy9QNHFVNmpaYUx3WEcxR0IzQnZ0MkhubzliWFIwRUlEczY4eHRidFNydm1qcUx3V2NmY3JtZHprdzIxVXFzeFVLM3prUVpURHYyL2tNSHR4cHJpNVd2dDJidWFqN2tOc21HTzZONUhrWUxaTTBWYXJabXNjQ3QrV0VES1lmdjV4Z2krckpTU1gyVTBPOVBmRk1mTUVyR1dmekE1NFZTUWZxSjM2NjZuQitVVlVaWFhsbDZlMGE1clhuZUtiYVpYcnQvU0s5d09lOVN2WmJHOEFlTmJUck4wSnhQd1dmUDlyTGNwU0NMcnBvVm0weVVYT3gxY3JaVUt6bS9ibEFzSkhZQ3RsdkJUMjgxN2RWOGJ5TjZnVmRpdTRnWlovU3Vsd2RybzhYY2hjcHpiVm81c3huN1o1anhxcHJKZWxYNElQRWI1Rlk4T00rWldGY1AzTThpWjViendmdHFVWlZYODlGNHRSanN6RVgzZlRYcjdpQjJqbHlmZWVaRDl6S3ZEYWNTcmppMkc2QnBKbmw5RmFPVUxzbDdpd0x1c3RZWWVLdjVJRUN4bC9lZFd3V2ROOEp2UGFYaUxSZlZXQzliandzMTNkK0UzR2pOM2lFSE1ya1hNYnVFMnJTcUtaN1pIdTE3YzBzMjI5VmphbmNtWTJlNG82N3VqQ2poOWE3ZXVXbkRiVjdnWStYeDNNYzI1UFkyb1h0bVduc2tHMjJvWjhOOC9sVmlzd0U4VzdNSDd1ZCtMY09yOHZPODJ0MmRDYlZDVi9mQ1g1UHBiQUkxWVQxdWpxWlRGT05kRitvczRGdUZHa2gzcSthQUx0Vkt0R3J6T3Z5c0pqcWM2bGpFYzRybncxU2Q4UnE1RlZqVnAzbCtzZ2E1Qi9WTnhqUklKdGhqMWVIWkVmQld6QlpWVjlNVjFMNkR3bXVqc04xR2NsM2RxY1NycWJEM1FObzhCcW9PWUg1Y3BUWUwyOU40Njdpei9hcmN0Y0NQRTdQTi9UWnpSWTZmWXFuTHdIKyt2cGl4Y1dzR2VUYnlobXB6RDNIdm0rM3RqZjZqK0RyVWNYT3VkRlp6cUFjdHF3N3haeTFWS3ZOYTl0RURGSXUwQk4ycjZjemlIK0I3ZzU0bWVMYXozdGlNVjJYQWhKNXNPQnJxOUtOdTJBcURlbm8xSi9QZklaZDkxRHRzbVdtcEQzcnpKUCtTdXZ3M1loaDRRWCtLekpweWhhNmZ5SVJab0hmaGd5NzRkcGpRcGZtVTFlMXJPYXkxbW51b0h3OWt6T3R2RmNuSHo1Ni9JZE95ZUk2UTlwWUIvLzBwYnk5cm8yVXVScnZldkFuK2hqaTBGZWpYWFZXYkQyaHZJV3BGcm41MFdHOE9jWkxTSk0vQnJzVXpNeUJ2ZW91UlphaWgzWVA4N0UrZUQvMTZBK0VBZWp6MEZqQXp1VXNtYWtGTjhjRWZlNWdER014S1IxN1hRQWJmcC9ScjM2aExpNW1zeDJCdmVlQ3ZZTTFRZWM3Uzk5VllFWFBQdDN3TGMxc010c2xneHc3eUIva1dQNXZlOXEwN2tLR1d2SVArVEZkSDlNdy9GcWtwOFdwKzJDM25Rd2I1em1uNW5CaGVueTF2MEpmejh3UHdYVUR2YmxVc3FNZDgzZ01mUXYrczNlRHBYcUZ0bi9HYm05QmJadnRiT1pXTE02aWZGY1ZvNDU3WEFSbkdZdWFEdmtQaGM2aUJVSXRndHJuWmc2SGZBajZ0cGd3NFFSMDhwTDd2d21lYTlOMzQyYjR5NzE3dUczYzJpL0hOM25GTER0WGRaVzdXdmFvanI5Y3h6S3RyM1dteVUvK0dlQjQxQnE5VHBUSVp6YnJ6OFhUUVhNak4rbEN1ekNaMHRCNHEzZWFvTVh1Wk5XaDFVaE4xR1B3RGZRWm1LVE41cHpqbDFCRFdJTzZnejFDTzM4ZXNkejNPRWg3TlNzVG52Vk9jQVczblpyN1Q5VXFGV0lJZXRvRFlYRGxOQldyZldpOVhvVzlWb3R5OGFJOGJ6ZUYwVzFuQ3EwMTcwcWpVaHRQREd1eG9EV2V6L3F4WmVaMDJSOTJKL0RqSjV2cWJNVEtydUJCdk1GY2QxbUNiQS9rWmdzNDdvejN5ZVEvTTErTnIrZndUZkxMZTBCbk9xdU9KWWdGRWc4bWtXYWtETHROYmVrS2RDS0gyUXg2TVB0N2hydU9mZnkrQ2VrQ1BvSSs3YWwvMTJXUStHL1Fuc3RtNEpaZlBHVkRYb1BiejJqaDROQUNYbFhxdXcwWDgvcXdPOVZuanNKNDJtdVBoVkxtSkZlZXpMSTlDcU5rOGJwaW9MNXVQL251MVZzSjh3K3RSTXBObnRiS2I1WWpJMmRQN2hzdjl1L0xoZmJjT2U3YXIrU1BFV3pORU5IeHVVNkdXd0h2elRUM1BaWTdUOTQ2MjZGK1ZiOVRrYTdtU3pKeGkxcXZxZjJ0bXJtLzJXZXdPNnNQajMrbnhzUGV4WDkrZStMeE00SHVXOS9YR3ZsOS9QajJEdmduZkc5a3NlZURQVHJOaVFudkV0RU5FTzhCOFZVeHJJTnFYR3FKOVJMU1Q1enp0QWV2YmlERnRIOUVPRU4rR2dtbVhtRFpHdEErWWRwdW5qZnVJYitlQWFOODdtQmJ4N2NpWWRvcG9COGkvblRLbTNTRGFGK1NIUHNidGlIQTdZdHo2R0xjand1MkljZXRqM0k0SXR5UEdyWTl3RzlRUmJqTEdiUmhqV29TYmpIRWJLcGgyaVdtUmY0Y1BtQmJocG1EY3BnaTN3YVNEYVJIZnFZeHBFVzRLeG0xYXhyUUlOd1hqdGtTNERkNFJiaXJHYlhuRXRBZzNGZU8yVkRFdHdrM0Z1QzB4YmtlRVd4bmpabURjamdpM01zYk53TGdkbDVnVytkZkF1QjBSYmc4WXR3M0M3YVhld2JTSTcwYkd0QWkzQjR6YnBveHBFVzRQR0xjdHd1MWxnbkI3eExodGo1Z1c0ZmFJY2R1cW1CYmg5b2h4MitaeDQ4Ly9nZmVxSEgvZ2lmbm4vUXpQWTBRclk5b3Bva1Yrbmp5WE1lMEcwU0kvVHhwN1JJdjgzRGdnUDA4YVIwdzdSTFFEekZmRnRBYWlSWDZlTkxDZjMvUDUwWWhSZmt3Nk1hYnRJMXFVSDVPT2dtbVhtQmI1dC9PQWFmUDUwVGlpL0pqME1XN0hEcVpGZlBzWXR5UEM3WWh4NjJQY2pnaTNJOFp0aUhBYm9EbWlJV1BjaGtkTWkzQ1RNVzVERmRNaTNHU00yeERoTnBoaytYRXg1LzVnZmxUTm1pdi9WdmpLRDA5REx3b013czl5dERBa0xPU2Zvc0FPczVOTnd6dWQ4bjRwbUo2eFRUN2x6dCtLMldyaCt2bmMweTQ5Ui83NkZTVGFqclloUW9vTnJJdWhvOUg4eVZoMkxuVjVOaW0wdTQ5Y1d4ejMzc3V5bkJkUy9PQlc4dDFONFM1aDd4RFRqcHgvakgvQ0xpK0Fhc0dHL0dQOEJiZVVmV2dFaExpaDViRVVDM0NlUWUyUHorSVFuVHZTRHp5ZkJNeE8zSm8vdUw1NHFFWE1LNElxOWdhY2xSN204b043OGRBa2F5MmlyUEMwMW1oSXpnL3ZuMkdybEd5VjBxMlMyQ3JPWDVrV2JrTnBiOW1HSlVVaGtUcXZSUkJiNU1zbDZhZU9jLzlITEJmM1FGZTAvUk9YWW5yRklEbnRKUkNUTk9RbnNTejJ1ZjkxejZORWMzbE02N1pyRnJrNDhRZGJ4b0xvd3JBUjRlWmt4bnlTeGJIdncwTTVzWXVmNkZaTDBoUnNZcFlkU3ZaYWlyMUlzbDNHRC9iQkpkemFuUjJ3U0tPUzVZWHNobGFHSDJGVjFITTlhcTlUS2JRMFNFbk9GZ0xaTXpSRytHZHhOU0IvNG4yNkdsQVFoN1cydzJOY09ZbDFJMGVIaFB4NnVnVHh0cUdlcnRHM1ZQaWJKMlNtK3VSMXFDY0VVa0l2cFhUQ0UrbmxqWngxSVF0c0Vhb25tNkNlbUhib2E4eXc3Z1FDd3JOQjBhQWVCSlFKcFlSNkd4Ni9PZDJLbThDTGZPU2FBamt3RXJnYXZYbURST3lSZkkzZnd3RFNrdFRuTXNGelBHeTRxcU9YeGZLdE5YcVp2b0t6QklxYTcwdFUwd2tLbTlTRW5EYU81bjgzWWhxdXBsT1NlVVNhdFFGMndNZFBITFhtRnoyU05kQ1NBZi9yRVpHQmlNTkNPUmMyRUdEeW15S25EVHdtMG9zSVY5RG5vYmxKNEhlSWc1bkw2a05KUHBmUUo0NFh4TksvKzlYLy9XN2tpWVRPeTFVZmYrRWNMeUl2SldnVGpUS3Jwdm1hWWJNWUtTT1hIaTgwU1RaSmx0Z2xHZWsySlBDcU1PM0FINzVBck4wUWRaRnQvV1NMeEs4Ui9iaWc1QVpHUGxUeFBhRXo2SUE2clNKUU9YSlhlcTRGNENtWXI2V2w2TEJTanVURVFBc0NqVjkzc2hseHhNNTAzZFBmaWNHUysxQzVCckFsY1k3b1EvcE9veEc1OGtUTUNQK043SURmbnZwZGJNK0kvL3lheS96ZitiY0FZaHQ3SjZUYWpyejU0cExLaFh2cXhLZGVqTUxMYzJrTWZ5VFhNN05rRnI3am5LK21iUmpTb2dIbVhmcHMwaHRMTlhnaXlya2YyRHNlMHFCL1Vzd21rOWZ4ZFlid3lXQkZCeFQ0Zms5TUMwR3lSK0o3U3RKRTZKdjRERExWMHBob0dPVGdndzlzQnZhUlpKZklMVjBEWk1VMUl6RDFyNnh3ZlhHL3ZqVUd6OVZlby82WkY2Qy9ybGVRTUE0TlJvc2lMSEFWS2JpRWxXeC85MUJpaHYvRzltOEJnVDcxV1pIUSt0cDIzNWp0RUtoU244c3lmZ1lwOVJiRzdodFhFSXIyWjE0NnJwQUFhejBDRTFqNCtVSG1QNWNrWHVCYm1odCsvdVgwMlBCZ0tBbzllQWhEakF1czRlY2lPaExqa3BnWEZRL21KMzZSRGpJSW1xMy83WjcwTlRlNEZvRVRXR0QrTUp4UUVYVG9WMUs2VDRJLy9QSWsxK0hFRk5Rd3dXMHAzT0tTR0hEMGVDVmhVVUI0RVQzMUI5NEorVTZYaUl0NElpQTlhRWtCNTN4alhBRGZGQVBDSjBuNEJ6YWRkNG5MSXE0ZHBHU0RsRzZRZEFJK2dnSUVveEtvVkpKRzRuRW9RYXZ6aVN2eGU1T3hWTXpFOExZUlFHYURuMEZYR0dCaGJHUGNacG4vZ2JZdWZBTk9MeXJDOXphTW55NVVuVXhjem81Y0J6cGovaDBqNWhib3hmbkZxVlpwcnBoMzBsNnpXVklMK0UyOHpEYU5RZG56ZVk4RnJ5WStUVFplMTRhbE45U0s0bTVjUGw4dXdtODY2bkc3SWRoMGptVUt2QkFFemdnQVN0NHpNNGJodllpT0tHU2VZeC81ZFVJL2Q5UDFhbnlleWg2OGJXRmQwaXQ3R1VIeUp2SDBTZjUwbVNUVFVZZFBCcWY3czk5cU1JQXJ1UGJ0bzRWZHRDcmd4Q2Vsc1NDVXhveDdHRDZheEdVMlJIYzI3WEwwazl1VDJYc0F2d2JLaVl6ayttV1NGUERxRWxFVGtKSW9XVE9KNHhSTGtVdEpHTjdZS01FdWw0QTg4OXRUNWtWYkVzTjFNdTZjejVYNU9TdnQzMy9lblRWSnp1NXM1YTRRK1p0QUF5YzFBOC9KWG9iNTIxMnlPdkhTRjdIL0I5eXFxTU5lTFFBQSJ9",
    "DCOS_PACKAGE_VERSION": "1.12.2",
    "DCOS_PACKAGE_NAME": "marathon-lb"
  },
  "portDefinitions": [
    {
      "port": 80,
      "protocol": "tcp"
    },
    {
      "port": 443,
      "protocol": "tcp"
    },
    {
      "port": 9090,
      "protocol": "tcp"
    },
    {
      "port": 9091,
      "protocol": "tcp"
    },
    {
      "port": 10000,
      "protocol": "tcp"
    },
    {
      "port": 10001,
      "protocol": "tcp"
    },
    {
      "port": 10002,
      "protocol": "tcp"
    },
    {
      "port": 10003,
      "protocol": "tcp"
    },
    {
      "port": 10004,
      "protocol": "tcp"
    },
    {
      "port": 10005,
      "protocol": "tcp"
    },
    {
      "port": 10006,
      "protocol": "tcp"
    },
    {
      "port": 10007,
      "protocol": "tcp"
    },
    {
      "port": 10008,
      "protocol": "tcp"
    },
    {
      "port": 10009,
      "protocol": "tcp"
    },
    {
      "port": 10010,
      "protocol": "tcp"
    },
    {
      "port": 10011,
      "protocol": "tcp"
    },
    {
      "port": 10012,
      "protocol": "tcp"
    },
    {
      "port": 10013,
      "protocol": "tcp"
    },
    {
      "port": 10014,
      "protocol": "tcp"
    },
    {
      "port": 10015,
      "protocol": "tcp"
    },
    {
      "port": 10016,
      "protocol": "tcp"
    },
    {
      "port": 10017,
      "protocol": "tcp"
    },
    {
      "port": 10018,
      "protocol": "tcp"
    },
    {
      "port": 10019,
      "protocol": "tcp"
    },
    {
      "port": 10020,
      "protocol": "tcp"
    },
    {
      "port": 10021,
      "protocol": "tcp"
    },
    {
      "port": 10022,
      "protocol": "tcp"
    },
    {
      "port": 10023,
      "protocol": "tcp"
    },
    {
      "port": 10024,
      "protocol": "tcp"
    },
    {
      "port": 10025,
      "protocol": "tcp"
    },
    {
      "port": 10026,
      "protocol": "tcp"
    },
    {
      "port": 10027,
      "protocol": "tcp"
    },
    {
      "port": 10028,
      "protocol": "tcp"
    },
    {
      "port": 10029,
      "protocol": "tcp"
    },
    {
      "port": 10030,
      "protocol": "tcp"
    },
    {
      "port": 10031,
      "protocol": "tcp"
    },
    {
      "port": 10032,
      "protocol": "tcp"
    },
    {
      "port": 10033,
      "protocol": "tcp"
    },
    {
      "port": 10034,
      "protocol": "tcp"
    },
    {
      "port": 10035,
      "protocol": "tcp"
    },
    {
      "port": 10036,
      "protocol": "tcp"
    },
    {
      "port": 10037,
      "protocol": "tcp"
    },
    {
      "port": 10038,
      "protocol": "tcp"
    },
    {
      "port": 10039,
      "protocol": "tcp"
    },
    {
      "port": 10040,
      "protocol": "tcp"
    },
    {
      "port": 10041,
      "protocol": "tcp"
    },
    {
      "port": 10042,
      "protocol": "tcp"
    },
    {
      "port": 10043,
      "protocol": "tcp"
    },
    {
      "port": 10044,
      "protocol": "tcp"
    },
    {
      "port": 10045,
      "protocol": "tcp"
    },
    {
      "port": 10046,
      "protocol": "tcp"
    },
    {
      "port": 10047,
      "protocol": "tcp"
    },
    {
      "port": 10048,
      "protocol": "tcp"
    },
    {
      "port": 10049,
      "protocol": "tcp"
    },
    {
      "port": 10050,
      "protocol": "tcp"
    },
    {
      "port": 10051,
      "protocol": "tcp"
    },
    {
      "port": 10052,
      "protocol": "tcp"
    },
    {
      "port": 10053,
      "protocol": "tcp"
    },
    {
      "port": 10054,
      "protocol": "tcp"
    },
    {
      "port": 10055,
      "protocol": "tcp"
    },
    {
      "port": 10056,
      "protocol": "tcp"
    },
    {
      "port": 10057,
      "protocol": "tcp"
    },
    {
      "port": 10058,
      "protocol": "tcp"
    },
    {
      "port": 10059,
      "protocol": "tcp"
    },
    {
      "port": 10060,
      "protocol": "tcp"
    },
    {
      "port": 10061,
      "protocol": "tcp"
    },
    {
      "port": 10062,
      "protocol": "tcp"
    },
    {
      "port": 10063,
      "protocol": "tcp"
    },
    {
      "port": 10064,
      "protocol": "tcp"
    },
    {
      "port": 10065,
      "protocol": "tcp"
    },
    {
      "port": 10066,
      "protocol": "tcp"
    },
    {
      "port": 10067,
      "protocol": "tcp"
    },
    {
      "port": 10068,
      "protocol": "tcp"
    },
    {
      "port": 10069,
      "protocol": "tcp"
    },
    {
      "port": 10070,
      "protocol": "tcp"
    },
    {
      "port": 10071,
      "protocol": "tcp"
    },
    {
      "port": 10072,
      "protocol": "tcp"
    },
    {
      "port": 10073,
      "protocol": "tcp"
    },
    {
      "port": 10074,
      "protocol": "tcp"
    },
    {
      "port": 10075,
      "protocol": "tcp"
    },
    {
      "port": 10076,
      "protocol": "tcp"
    },
    {
      "port": 10077,
      "protocol": "tcp"
    },
    {
      "port": 10078,
      "protocol": "tcp"
    },
    {
      "port": 10079,
      "protocol": "tcp"
    },
    {
      "port": 10080,
      "protocol": "tcp"
    },
    {
      "port": 10081,
      "protocol": "tcp"
    },
    {
      "port": 10082,
      "protocol": "tcp"
    },
    {
      "port": 10083,
      "protocol": "tcp"
    },
    {
      "port": 10084,
      "protocol": "tcp"
    },
    {
      "port": 10085,
      "protocol": "tcp"
    },
    {
      "port": 10086,
      "protocol": "tcp"
    },
    {
      "port": 10087,
      "protocol": "tcp"
    },
    {
      "port": 10088,
      "protocol": "tcp"
    },
    {
      "port": 10089,
      "protocol": "tcp"
    },
    {
      "port": 10090,
      "protocol": "tcp"
    },
    {
      "port": 10091,
      "protocol": "tcp"
    },
    {
      "port": 10092,
      "protocol": "tcp"
    },
    {
      "port": 10093,
      "protocol": "tcp"
    },
    {
      "port": 10094,
      "protocol": "tcp"
    },
    {
      "port": 10095,
      "protocol": "tcp"
    },
    {
      "port": 10096,
      "protocol": "tcp"
    },
    {
      "port": 10097,
      "protocol": "tcp"
    },
    {
      "port": 10098,
      "protocol": "tcp"
    },
    {
      "port": 10099,
      "protocol": "tcp"
    },
    {
      "port": 10100,
      "protocol": "tcp"
    }
  ],
  "args": [
    "sse",
    "-m",
    "http://marathon.mesos:8080",
    "--health-check",
    "--haproxy-map",
    "--max-reload-retries",
    "10",
    "--reload-interval",
    "10",
    "--group",
    "external"
  ],
  "requirePorts": true,
  "upgradeStrategy": {
    "maximumOverCapacity": 0.2,
    "minimumHealthCapacity": 0.5
  }
}
```

除以下参数外，请勿随意修改其他参数，如果确实需要修改，请先参考[Marathon配置项](https://docs.d2iq.com/mesosphere/dcos/1.11/deploying-services/marathon-parameters/)

> cpus：marathon-LB服务的cpu限制，LB服务访问压力较高，请分配DC/OS集群中public agent节点的大部分资源。public agent节点总cpu资源为4，所以此处设置为3

> mem：marathon-LB服务的cpu限制，LB服务访问压力较高，请分配DC/OS集群中public agent节点的大部分资源。public agent节点总内存资源为4G，所以此处设置为3072MiB

> instances：marathon-LB容器个数，设置为集群内public agent节点的个数

> args：部分服务参数，请不要随意修改。group设置为external，则集群使用时，label中设置了"HAPROXY_GROUP": "external"的服务将被纳入此marathon-LB。如果有多组marathon-LB，您可以再设置一个额外的group，例如internal组
