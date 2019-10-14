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
package net.atayun.bazooka.upms.biz.domain;

import lombok.*;
import net.atayun.bazooka.upms.api.dto.req.RoleAddReqDTO;
import net.atayun.bazooka.upms.api.dto.req.RoleEditReqDTO;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 角色
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "upms_role")
@Entity
public class Role  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 角色名
     */
    @Column(name = "role_name")
    private String roleName;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "upms_role_permission", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
    private Set<Permission> permissions = new HashSet<>();


    public Role(String roleName) {
        this();
        this.roleName = roleName;
    }

    public Role(RoleAddReqDTO roleAddReqDTO) {
        this(roleAddReqDTO.getRoleName());
        this.remark = roleAddReqDTO.getRemark();
    }

    public Role(RoleEditReqDTO roleEditReqDTO) {
        setId(roleEditReqDTO.getRoleId());
        this.roleName = roleEditReqDTO.getRoleName();
        this.remark = roleEditReqDTO.getRemark();
    }

    public Long getRoleId() {
        return getId();
    }
}
