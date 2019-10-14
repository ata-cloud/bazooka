package net.atayun.bazooka.upms.biz.domain;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 上午9:18
 */

import com.youyu.common.entity.BaseEntity;

import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class JdbcMysqlEntity extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public JdbcMysqlEntity(Long id, String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime) {
        super(createAuthor, createTime, updateAuthor, updateTime);
        this.id = id;
    }


    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long aLong) {
        this.id = id;

    }

    public JdbcMysqlEntity() {
    }

    public JdbcMysqlEntity(Long id) {
        this.id = id;
    }
}
