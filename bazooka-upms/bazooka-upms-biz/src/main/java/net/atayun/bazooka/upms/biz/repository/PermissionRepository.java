package net.atayun.bazooka.upms.biz.repository;

import net.atayun.bazooka.upms.biz.domain.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long> {

    long countByUrl(String url);

    List<Permission> findPermissionsByParentId(Long parentId);

    List<Permission> findAllByPermissionIdAndPermissionName(Long permissoionId, String permissionNam, Pageable pageable);

}
