package tech.fanpu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.fanpu.po.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByOpenId(String openId);

    User findByPhone(String phone);

}
