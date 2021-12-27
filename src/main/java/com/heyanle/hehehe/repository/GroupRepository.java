package com.heyanle.hehehe.repository;

import com.heyanle.hehehe.entity.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/30 20:39.
 * https://github.com/heyanLE
 */
@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {

    @Query(value = "select * from chat_group where exists(" +
            "select * from group_member where group_member.group_id=chat_group.id and group_member.member_username=?)",nativeQuery = true)
    List<Group> getAllGroupByMemberUsername(String username);

    @Override
    Optional<Group> findById(Long aLong);

    List<Group> findAllBy();
}
