package net.atayun.bazooka.pms.biz.repository;

import net.atayun.bazooka.pms.biz.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
public interface ProjectUserRepository extends JpaRepository<App, Integer> {

}
