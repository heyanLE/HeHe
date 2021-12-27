package com.heyanle.hehehe.repository;

import com.heyanle.hehehe.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by HeYanLe on 2021/5/30 20:41.
 * https://github.com/heyanLE
 */
@Repository
public interface GroupMessageRepository extends CrudRepository<GroupMessage, Long> {

    Page<GroupMessage> findByGroupIdAndTimeLessThanOrderByTimeDesc(Long groupId, Long time, Pageable pageable);


    Long countAllByGroupId(Long groupId);

}
