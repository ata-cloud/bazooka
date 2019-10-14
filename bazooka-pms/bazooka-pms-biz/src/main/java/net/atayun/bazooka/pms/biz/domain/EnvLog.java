package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:16
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class EnvLog extends Env {


    /**
     * 操作类型(0:新增，1:修改，2:删除)
     */
    @Column(name = "opt_type")
    private String optType;

}
