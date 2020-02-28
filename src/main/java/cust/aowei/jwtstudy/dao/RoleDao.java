package cust.aowei.jwtstudy.dao;

import cust.aowei.jwtstudy.model.Role;
import org.springframework.stereotype.Repository;

/**
 * @author aowei
 */
@Repository
public class RoleDao {

    public Role findByUsername(String username) {
        Role role =new Role();
        role.setRoleId("111");
        role.setRoleName("all-admin");
        return role;
    }
}
