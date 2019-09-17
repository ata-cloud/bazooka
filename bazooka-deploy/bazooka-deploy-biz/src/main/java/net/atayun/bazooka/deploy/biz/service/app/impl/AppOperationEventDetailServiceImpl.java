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
package net.atayun.bazooka.deploy.biz.service.app.impl;

import net.atayun.bazooka.deploy.biz.config.DeployLogProperties;
import net.atayun.bazooka.deploy.biz.dal.dao.app.AppOperationEventDetailMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventDetailEntity;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperationEventDetailDto;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventDetailService;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.File;

import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.LOG_PATH_ERR_CODE;

/**
 * @author Ping
 * @date 2019-08-01
 */
@Slf4j
@Service
public class AppOperationEventDetailServiceImpl
        extends AbstractService<Long, AppOperationEventDetailDto, AppOperationEventDetailEntity, AppOperationEventDetailMapper>
        implements AppOperationEventDetailService {

    @Autowired
    private DeployLogProperties deployLogProperties;

    /**
     * insertByParam
     *
     * @param eventId     eventId
     * @param eventRemark eventRemark
     */
    @Override
    public void insertByParam(Long eventId, String eventRemark) {
        String logFullPath = deployLogProperties.getRootPath() + File.separator + eventId;
        File file = new File(logFullPath);

        boolean pathSuccess = true;
        if (!file.exists()) {
            pathSuccess = file.mkdirs();
        }

        if (!pathSuccess) {
            throw new BizException(LOG_PATH_ERR_CODE, "logPath创建失败");
        }
        log.info("事件logPath已准备好. [logFullPath: {}]", logFullPath);

        AppOperationEventDetailEntity appOperationEventDetailEntity = new AppOperationEventDetailEntity();
        appOperationEventDetailEntity.setEventId(eventId);
        if (StringUtils.hasText(eventRemark)) {
            eventRemark = String.join(" ", "(", eventRemark, ")");
        }
        appOperationEventDetailEntity.setRemark(eventRemark);
        appOperationEventDetailEntity.setLogDir(logFullPath);

        getMapper().insertSelective(appOperationEventDetailEntity);
    }

    /**
     * 通过EventId查询
     *
     * @param eventId eventId
     * @return AppOperationEventDetailEntity
     */
    @Override
    public AppOperationEventDetailEntity selectByEventId(Long eventId) {
        Example example = new Example(AppOperationEventDetailEntity.class);
        example.createCriteria().andEqualTo("eventId", eventId);
        return getMapper().selectOneByExample(example);
    }
}
