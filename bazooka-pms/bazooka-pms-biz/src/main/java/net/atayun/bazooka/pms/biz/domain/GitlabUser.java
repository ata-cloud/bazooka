package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class GitlabUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;

    private Long gitlabUserId;
}
