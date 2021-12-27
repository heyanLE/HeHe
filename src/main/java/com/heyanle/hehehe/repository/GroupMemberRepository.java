package com.heyanle.hehehe.repository;

import com.heyanle.hehehe.entity.GroupMemberItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/30 20:41.
 * https://github.com/heyanLE
 */
@Repository
public interface GroupMemberRepository extends CrudRepository<GroupMemberItem, Long> {

    @Override
    Optional<GroupMemberItem> findById(Long aLong);

    Optional<GroupMemberItem> findFirstByGroupIdAndMemberUsername(Long groupId, String username);

    Long countAllByGroupId(Long groupId);

    List<GroupMemberItem> findAllBy();
}
