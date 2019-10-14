package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/10 上午10:08
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class EnvProjectPortDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
