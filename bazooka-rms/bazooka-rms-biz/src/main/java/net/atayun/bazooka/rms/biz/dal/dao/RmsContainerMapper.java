package net.atayun.bazooka.rms.biz.dal.dao;

import com.youyu.common.mapper.YyMapper;
import net.atayun.bazooka.rms.biz.dal.entity.ContainerAndResourceSumEntity;
import net.atayun.bazooka.rms.biz.dal.entity.ResourceSumEntity;
import net.atayun.bazooka.rms.biz.dal.entity.ResourceSumEnvGroupEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsContainer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Ping
 */
public interface RmsContainerMapper extends YyMapper<RmsContainer> {

    /**
     * sumResourceByClusterId
     *
     * @param clusterId clusterId
     * @return ResourceSumEntity
     */
    @Select("select " +
            "sum(cpu) cpu, " +
            "sum(memory) memory, " +
            "sum(disk) disk " +
            "from rms_container where cluster_id = #{clusterId} and container_status = '3' group by cluster_id;")
    ResourceSumEntity selectResourceByClusterId(@Param("clusterId") Long clusterId);

    /**
     * sumResourceByClusterIdGroupByEnv
     *
     * @param clusterId clusterId
     * @return
     */
    @Select("select " +
            "rc.env_id id, " +
            "re.code, " +
            "re.name, " +
            "sum(rc.cpu) cpus, " +
            "sum(rc.memory) memory, " +
            "sum(rc.disk) disk " +
            "from rms_container rc " +
            "inner join rms_env re on rc.env_id = re.id and rc.cluster_id = re.cluster_id " +
            "where rc.cluster_id = #{clusterId} and rc.container_status = '3' " +
            "group by rc.env_id, re.name, re.code;")
    List<ResourceSumEnvGroupEntity> sumResourceByClusterIdGroupByEnv(@Param("clusterId") Long clusterId);

    /**
     * sumContainerAndResourceByNode
     *
     * @param nodeId nodeId
     * @return ContainerAndResourceSumEntity
     */
    @Select("select " +
            "count(container_name) count, " +
            "sum(cpu) cpu, " +
            "sum(memory) memory, " +
            "sum(disk) disk " +
            "from rms_container where node_id = #{nodeId} and container_status = '3' group by node_id;")
    ContainerAndResourceSumEntity sumContainerAndResourceByNode(@Param("nodeId") String nodeId);

    /**
     * updateStatusByAppId
     *
     * @param envId  envId
     * @param appId  appId
     * @param status status
     */
    @Update("update rms_container " +
            " set container_status = #{status} " +
            " where env_id = #{envId} and app_id = #{appId};")
    void updateStatusByEnvIdAndAppId(@Param("envId") Long envId, @Param("appId") Long appId, @Param("status") String status);
}
