package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atayun.bazooka.upms.biz.domain.User;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:21
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
