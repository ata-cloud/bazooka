package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atayun.bazooka.upms.biz.domain.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:23
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class UserSort {

    private User user;

    @ManyToOne
    @JoinColumn
    private Project project;
    
    @ManyToOne
    @JoinColumn
    private App app;

}
