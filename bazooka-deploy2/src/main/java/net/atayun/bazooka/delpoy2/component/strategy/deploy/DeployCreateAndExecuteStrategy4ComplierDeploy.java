package net.atayun.bazooka.delpoy2.component.strategy.deploy;

import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.combase.annotation.StrategyNum;
import net.atayun.bazooka.delpoy2.dal.dao.AppEnvDeployConfigMapper;
import net.atayun.bazooka.delpoy2.dal.entity.AppEnvDeployConfig;
import net.atayun.bazooka.delpoy2.dal.entity.DeployEntity;
import net.atayun.bazooka.delpoy2.dal.dao.DeployCommandMapper;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 下午1:33
 * 多流程执行策略
 */
@Slf4j
@Component
@StrategyNum(superClass = DeployCreateAndExecuteStrategy.class, number = "ComplierDEPLOY", describe = "构建发布")
public class DeployCreateAndExecuteStrategy4ComplierDeploy extends DeployCreateAndExecuteStrategy {

    @Autowired
    private DeployCommandMapper deployCommandMapper;
    @Autowired
    private AppEnvDeployConfigMapper appEnvDeployConfigMapper;

//    START("启动服务",AppOperationEventLogTypeEnum.APP_START, true),
//    RESTART("重启服务", AppOperationEventLogTypeEnum.APP_RESTART, true),
//    SCALE("扩/缩容服务", AppOperationEventLogTypeEnum.APP_SCALE, true),
//    STOP("关闭服务", AppOperationEventLogTypeEnum.APP_SHUTDOWN, true),
//
//    DEPLOY("发布", AppOperationEventLogTypeEnum.APP_DEPLOY, true),
//    ROLLBACK("回滚", AppOperationEventLogTypeEnum.APP_ROLLBACK, true),
//
//    PUSH_IMAGE("镜像推送", AppOperationEventLogTypeEnum.PUSH_DOCKER_IMAGE, false),
//    DELETE_IMAGE("删除镜像", AppOperationEventLogTypeEnum.DELETE_DOCKER_IMAGE, false);


    @Override
    protected DeployEntity create(DeployCommandReqDto deployCommandReqDto) {
        return null;
    }

    /**
     * 构造 构建发布命令
     *
     * @param deployCommandReqDto
     * @return
     */
    @Override
    protected DeployEntity buildDeployCommand(DeployCommandReqDto deployCommandReqDto) {
        Long appEnvDeployConfigId = deployCommandReqDto.getDeployConfigId();
        AppEnvDeployConfig appEnvDeployConfig = appEnvDeployConfigMapper.getById(appEnvDeployConfigId);
        Long clusterId = appEnvDeployConfig.getClusterId();
        DeployEntity deployEntity = new DeployEntity();
        deployEntity.setBranch(deployEntity.getBranch());
        deployEntity.setRemark("发布配置: " + deployEntity.getPublish_config());
        return deployEntity;
    }
}
