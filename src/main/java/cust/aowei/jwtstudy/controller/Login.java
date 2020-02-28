package cust.aowei.jwtstudy.controller;

import cust.aowei.jwtstudy.model.Result;
import cust.aowei.jwtstudy.model.ResultCode;
import cust.aowei.jwtstudy.model.Role;
import cust.aowei.jwtstudy.model.User;
import cust.aowei.jwtstudy.service.RoleService;
import cust.aowei.jwtstudy.service.UserService;
import cust.aowei.jwtstudy.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aowei
 */
@RestController
public class Login {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result login(String username, String password){
        User user = userService.findByUsername(username);
        if(user == null || !user.getPassword().equals(password)){
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
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
            String token = jwtUtils.createJwt(username,username,map);

            Claims claims = jwtUtils.parseJwt(token);
            System.out.println(claims.getId());
            System.out.println(claims.getSubject());
            System.out.println(claims.get("roles"));

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
    public Result test(){
        return new Result(ResultCode.SUCCESS);
    }
}
