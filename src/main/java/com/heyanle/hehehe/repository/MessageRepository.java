package com.heyanle.hehehe.repository;

import com.heyanle.hehehe.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by HeYanLe on 2021/5/30 20:42.
 * https://github.com/heyanLE
 */
@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {


    @Query(
            value = "select * from message where time<=?3 and((from_username=?1 and to_username=?2) or (from_username=?2 and to_username=?1))",
            countQuery = "select count(*) from message where time<=?3 and((from_username=?1 and to_username=?2) or (from_username=?2 and to_username=?1))",
            nativeQuery = true
    )
    Page<Message> findByUsername(String username, String friend, Long time, Pageable pageable);

    @Query(value = "select count(*) from message where (from_username=?1 and to_username=?2) or (from_username=?2 and to_username=?1)", nativeQuery = true)
    Long countByUsername(String username);

}
