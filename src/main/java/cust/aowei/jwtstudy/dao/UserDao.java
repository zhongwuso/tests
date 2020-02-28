package cust.aowei.jwtstudy.dao;

import cust.aowei.jwtstudy.model.User;
import org.springframework.stereotype.Repository;

/**
 * @author aowei
 */
@Repository
public class UserDao {
    public User findByUsername(String username) {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");
        return user;
    }
}
