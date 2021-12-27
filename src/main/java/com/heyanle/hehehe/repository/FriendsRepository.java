package com.heyanle.hehehe.repository;

import com.heyanle.hehehe.entity.Account;
import com.heyanle.hehehe.entity.FriendsItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/30 20:40.
 * https://github.com/heyanLE
 */
@Repository
public interface FriendsRepository extends CrudRepository<FriendsItem, Long> {

    Optional<FriendsItem> getFirstByAccountIAndAccountII(String usernameI, String usernameII);

}
