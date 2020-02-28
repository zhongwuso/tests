package cust.aowei.jwtstudy.controller;

import cust.aowei.jwtstudy.annotation.JwtIgnore;
import cust.aowei.jwtstudy.exception.CustomException;
import cust.aowei.jwtstudy.model.Result;
import cust.aowei.jwtstudy.model.ResultCode;
import cust.aowei.jwtstudy.model.Role;
import cust.aowei.jwtstudy.model.User;
import cust.aowei.jwtstudy.service.RoleService;
import cust.aowei.jwtstudy.service.UserService;
import cust.aowei.jwtstudy.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aowei
 */
@Slf4j
@RestController
public class Login {
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @JwtIgnore
    public Result login(String username, String password) throws CustomException {
        User user = userService.findByUsername(username);
        if(user == null || !user.getPassword().equals(password)){
            return new Result(ResultCode.FAIL);
        } else {
//             登录成功
//             获取到所有的可访问api权限
//            StringBuilder sb = new StringBuilder();
//            for(Role role : user.getRole()){
//                for(Permission perm : role.getPermissions()){
//                    sb.append(perm.getCode().append(","));
//                }
//            }

            Role role = roleService.findByUsername(username);
            Map<String,Object> map = new HashMap<>();
            // 可访问的角色
            map.put("roles",role.getRoleName());
            String token = JwtUtils.createJwt(username,username,map);
//            Claims claims = JwtUtils.parseJwt(token);
//            System.out.println("角色："+claims.get("roles"));
//            System.out.println("用户名："+claims.getSubject());
//            System.out.println("用户id："+claims.getId());
            Result result = new Result(ResultCode.SUCCESS);
            result.setData(token);
            return result;
        }
    }

    // 测试需要权限的url（需要登录且拥有test2的权限）
    @RequestMapping(value = "/test3",method = RequestMethod.GET,name="all-admin")
    public Result test3(){
        return new Result(ResultCode.SUCCESS);
    }

    // 测试需要权限的url（需要登录且拥有test2的权限）
    @RequestMapping(value = "/test2",method = RequestMethod.GET,name="test2")
    public Result test1(){
        return new Result(ResultCode.SUCCESS);
    }

    // 测试被拦截的url(需要登录)
    @RequestMapping(value = "/test1",method = RequestMethod.GET)
    public Result test2(){
        return new Result(ResultCode.SUCCESS);
    }

    // 测试不拦截的url（不需要登录）
    @RequestMapping("/test")
    @JwtIgnore
    public Result test(){
        return new Result(ResultCode.SUCCESS);
    }
}
