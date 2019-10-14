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
package net.atayun.bazooka.pms.biz.controller;


import net.atayun.bazooka.pms.api.api.RmsClusterNodeApi;
import net.atayun.bazooka.pms.api.dto.req.ClusterNodeReqDto;
import net.atayun.bazooka.pms.api.dto.rsp.ClusterNodeRspDto;
import net.atayun.bazooka.pms.biz.service.RmsClusterNodeService;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.youyu.common.api.Result.ok;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群节点 controller
 */
@RestController
@RequestMapping("/clusterNode")
public class RmsClusterNodeController implements RmsClusterNodeApi {

    @Autowired
    private RmsClusterNodeService rmsClusterNodeService;

    @Override
    @PostMapping("/getClusterNodePage")
    public Result<PageData<ClusterNodeRspDto>> getClusterNodePage(@RequestBody ClusterNodeReqDto clusterNodeReqDto) {
        return ok(rmsClusterNodeService.getClusterNodePage(clusterNodeReqDto));
    }
}
