package cust.aowei.jwtstudy.service;

import cust.aowei.jwtstudy.dao.RoleDao;
import cust.aowei.jwtstudy.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleDao roleDao;
    public Role findByUsername(String username) {
        Role role = roleDao.findByUsername(username);
        return role;
    }
}
