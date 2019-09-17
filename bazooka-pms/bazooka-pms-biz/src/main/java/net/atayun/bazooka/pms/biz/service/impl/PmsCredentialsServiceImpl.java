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
package net.atayun.bazooka.pms.biz.service.impl;

import net.atayun.bazooka.pms.api.dto.PmsCredentialsDto;
import net.atayun.bazooka.pms.biz.dal.dao.PmsCredentialsMapper;
import net.atayun.bazooka.pms.biz.dal.entity.PmsCredentialsEntity;
import net.atayun.bazooka.pms.biz.service.PmsCredentialsService;
import com.youyu.common.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author WangSongJun
 * @date 2019-08-27
 */
@Slf4j
@Service
public class PmsCredentialsServiceImpl extends AbstractService<Long, PmsCredentialsDto, PmsCredentialsEntity, PmsCredentialsMapper> implements PmsCredentialsService {

}
