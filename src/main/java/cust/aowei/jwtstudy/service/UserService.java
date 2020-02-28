package cust.aowei.jwtstudy.service;

import cust.aowei.jwtstudy.dao.UserDao;
import cust.aowei.jwtstudy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);
        return user;
    }
}
