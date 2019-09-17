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
package net.atayun.bazooka.upms.biz.dal.entity;

import net.atayun.bazooka.base.bean.SpringContextBean;
import net.atayun.bazooka.upms.api.dto.req.PermissionAddReqDTO;
import net.atayun.bazooka.upms.api.dto.req.PermissionEditReqDTO;
import net.atayun.bazooka.upms.api.dto.rsp.PermissionTreeRspDTO;
import net.atayun.bazooka.upms.api.dto.rsp.UserMenuPermissionRspDTO;
import net.atayun.bazooka.upms.biz.component.realm.ShiroAuthRealm;
import net.atayun.bazooka.upms.biz.dal.dao.PermissionMapper;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.upms.biz.helper.constant.PermissionConstant;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty4List;
import static net.atayun.bazooka.upms.api.enums.PermissionCodeEnum.SYS;
import static net.atayun.bazooka.upms.api.enums.PermissionTypeEnum.MENU;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限
 */
@Setter
@Getter
@Table(name = "upms_permission")
public class Permission extends JdbcMysqlEntity<Long> {

    /**
     * 权限编码
     */
    @Column(name = "code")
    private String code;

    /**
     * 权限名称
     */
    @Column(name = "permission_name")
    private String permissionName;

    /**
     * 权限url
     */
    @Column(name = "url")
    private String url;

    /**
     * 权限类型(0:菜单 1:按钮 2:iframe 3:其他)
     * 注:后续扩展
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 图标
     */
    @Column(name = "icon")
    private String icon;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 父级权限
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 排序
     */
    @Column(name = "rank")
    private Integer rank;

    /**
     * iframe地址
     */
    @Column(name = "iframe_url")
    private String iframeUrl;

    /**
     * iframe数据
     */
    @Column(name = "iframe_json")
    private String iframeJson;

    public Permission() {
    }

    public Permission(PermissionAddReqDTO permissionAddReqDTO) {
        this.code = defaultIfBlank(permissionAddReqDTO.getCode(), SYS.getCode());
        this.permissionName = permissionAddReqDTO.getPermissionName();
        this.url = permissionAddReqDTO.getUrl();
        this.type = permissionAddReqDTO.getType();
        this.icon = permissionAddReqDTO.getIcon();
        this.remark = permissionAddReqDTO.getRemark();
        this.parentId = isNull(permissionAddReqDTO.getParentId()) ? PermissionConstant.ROOT_PERMISSION : permissionAddReqDTO.getParentId();
        this.rank = permissionAddReqDTO.getRank();
        this.iframeUrl = permissionAddReqDTO.getIframeUrl();
        this.iframeJson = permissionAddReqDTO.getIframeJson();
    }

    public Permission(PermissionEditReqDTO permissionEditReqDTO) {
        setId(permissionEditReqDTO.getPermissionId());
        this.code = defaultIfBlank(permissionEditReqDTO.getCode(), SYS.getCode());
        this.permissionName = permissionEditReqDTO.getPermissionName();
        this.url = permissionEditReqDTO.getUrl();
        this.type = permissionEditReqDTO.getType();
        this.icon = permissionEditReqDTO.getIcon();
        this.remark = permissionEditReqDTO.getRemark();
        this.parentId = isNull(permissionEditReqDTO.getParentId()) ? PermissionConstant.ROOT_PERMISSION : permissionEditReqDTO.getParentId();
        this.rank = permissionEditReqDTO.getRank();
        this.iframeUrl = permissionEditReqDTO.getIframeUrl();
        this.iframeJson = permissionEditReqDTO.getIframeJson();
    }

    public String getName() {
        return this.permissionName;
    }

    public String getPath() {
        return this.url;
    }

    /**
     * 获取子菜单权限列表
     *
     * @return
     */
    public List<UserMenuPermissionRspDTO> getChildrenMenu() {
        Long userId = ShiroAuthRealm.getUserId();
        PermissionMapper permissionMapper = SpringContextBean.getBean(PermissionMapper.class);
        List<Permission> permissions = permissionMapper.getPermissionByUserIdParentIdPermissionType(userId, getId(), MENU.getCode());
        return copyProperty4List(permissions, UserMenuPermissionRspDTO.class);
    }

    /**
     * 获取权限树列表
     *
     * @return
     */
    public List<PermissionTreeRspDTO> getChildrenTree() {
        PermissionMapper permissionMapper = SpringContextBean.getBean(PermissionMapper.class);
        List<Permission> permissions = permissionMapper.getPermissionByParentId(getId());
        return copyProperty4List(permissions, PermissionTreeRspDTO.class);
    }

    public Long getPermissionId() {
        return getId();
    }
}
