package com.heyanle.hehehe.repository;

import com.heyanle.hehehe.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/30 20:36.
 * https://github.com/heyanLE
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findFirstByUsername(String username);

    @Query(value = "select * from account where exists " +
            "(select * from friends where account.username=friends.account_ii and friends.account_i=?)", nativeQuery = true)
    List<Account> getAllFriendByUsername(String username);

    @Query(value = "select * from account where exists " +
            "(select * from group_member where group_member.member_username=account.username and group_member.group_id=?)", nativeQuery = true)
    List<Account> getAllInGroup(Long groupId);

}
