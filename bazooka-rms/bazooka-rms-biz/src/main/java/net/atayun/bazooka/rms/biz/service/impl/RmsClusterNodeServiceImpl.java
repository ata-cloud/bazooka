/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.rms.biz.service.impl;

import net.atayun.bazooka.base.dcos.dto.NodeDto;
import net.atayun.bazooka.base.dcos.dto.NodeHealthDto;
import net.atayun.bazooka.base.dcos.dto.NodeHealthInfoDto;
import net.atayun.bazooka.base.dcos.dto.NodeSlaveDto;
import net.atayun.bazooka.base.service.BatchService;
import net.atayun.bazooka.rms.api.dto.RmsClusterNodeDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterNodeReqDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterNodeRspDto;
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterNodeMapper;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterNodeEntity;
import net.atayun.bazooka.rms.biz.service.RmsClusterNodeService;
import com.youyu.common.api.PageData;
import com.youyu.common.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.atayun.bazooka.base.constant.CommonConstants.PROTOCOL;
import static net.atayun.bazooka.base.dcos.DcosServerBean.NODE_URL_HEALTH_SUFFIX;
import static net.atayun.bazooka.base.dcos.DcosServerBean.NODE_URL_SUFFIX;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty4List;
import static net.atayun.bazooka.base.utils.StringUtil.eq;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.join;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群节点 service impl
 */
@Slf4j
@Service
public class RmsClusterNodeServiceImpl extends AbstractService<Long, RmsClusterNodeDto, RmsClusterNodeEntity, RmsClusterNodeMapper> implements RmsClusterNodeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BatchService batchService;

    @Autowired
    private RmsClusterNodeMapper rmsClusterNodeMapper;


    @Override
    public List<RmsClusterNodeEntity> refreshClusterNodeInfo(RmsClusterConfigEntity rmsClusterConfigEntity) {
        try {
            String url = join(PROTOCOL, rmsClusterConfigEntity.getUrl());
            NodeDto node = getNode(url);
            NodeHealthDto nodeHealth = getNodeHealth(url);
            return doRefreshClusterNodeInfo(node, nodeHealth, rmsClusterConfigEntity);
        } catch (Exception ex) {
            log.error("刷新集群地址:[{}]对应节点异常信息:[{}]", rmsClusterConfigEntity.getUrl(), getFullStackTrace(ex));
            return null;
        }
    }

    @Override
    public List<ClusterNodeRspDto> getAllClusterNodes(Long clusterId) {
        List<ClusterNodeRspDto> list = new ArrayList<>();
        Example example = new Example(RmsClusterNodeEntity.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("clusterId",clusterId);
        List<RmsClusterNodeEntity> rmsClusterNodeEntities = rmsClusterNodeMapper.selectByExample(example);
        list = copyProperty4List(rmsClusterNodeEntities, ClusterNodeRspDto.class);
        return list;
    }

    @Override
    public PageData<ClusterNodeRspDto> getClusterNodePage(ClusterNodeReqDto clusterNodeReqDto) {
        PageData<ClusterNodeRspDto> pageData = new PageData<>(clusterNodeReqDto.getPageNo(), clusterNodeReqDto.getPageSize());
        RmsClusterNodeEntity queryRmsClusterNodeEntity = new RmsClusterNodeEntity();
        queryRmsClusterNodeEntity.setClusterId(clusterNodeReqDto.getClusterId());
        List<RmsClusterNodeEntity> rmsClusterNodeEntities = rmsClusterNodeMapper.selectInPage(queryRmsClusterNodeEntity, pageData);

        pageData.setRows(copyProperty4List(rmsClusterNodeEntities, ClusterNodeRspDto.class));
        return pageData;
    }

    /**
     * 获取节点健康信息
     *
     * @param url
     * @return
     */
    private NodeHealthDto getNodeHealth(String url) {
        try {
            String nodeHealthJson = restTemplate.getForObject(join(url, NODE_URL_HEALTH_SUFFIX), String.class);
            return parseObject(nodeHealthJson, NodeHealthDto.class);
        } catch (Exception ex) {
            log.error("获取地址:[{}]对应异常信息:[{}]", url, getFullStackTrace(ex));
            return new NodeHealthDto();
        }
    }

    /**
     * 获取节点信息
     *
     * @param url
     * @return
     */
    private NodeDto getNode(String url) {
        String nodeJson = restTemplate.getForObject(join(url, NODE_URL_SUFFIX), String.class);
        return parseObject(nodeJson, NodeDto.class);
    }

    /**
     * 执行刷新集群节点信息
     *
     * @param node
     * @param nodeHealth
     * @param rmsClusterConfigEntity
     */
    private List<RmsClusterNodeEntity> doRefreshClusterNodeInfo(NodeDto node, NodeHealthDto nodeHealth, RmsClusterConfigEntity rmsClusterConfigEntity) {
        List<NodeSlaveDto> slaves = node.getSlaves();
        if (isEmpty(slaves)) {
            return null;
        }

        List<RmsClusterNodeEntity> rmsClusterNodeEntities = new ArrayList<>();
        List<NodeHealthInfoDto> nodeHealths = nodeHealth.getNodes();
        for (NodeSlaveDto nodeSlave : slaves) {
            if (isEmpty(nodeHealths)) {
                rmsClusterNodeEntities.add(new RmsClusterNodeEntity(nodeSlave, rmsClusterConfigEntity));
            } else {
                NodeHealthInfoDto nodeHealthInfo = nodeHealths.stream().filter(nodeHealthInfoDto -> eq(nodeHealthInfoDto.getHostIp(), nodeSlave.getHostname())).findFirst().get();
                rmsClusterNodeEntities.add(new RmsClusterNodeEntity(nodeSlave, nodeHealthInfo, rmsClusterConfigEntity));
            }
        }
        rmsClusterNodeMapper.deleteClusterNodeByClusterId(rmsClusterConfigEntity.getClusterId());
        batchService.batchDispose(rmsClusterNodeEntities, RmsClusterNodeMapper.class, "insertSelective");
        return rmsClusterNodeEntities;
    }
}




