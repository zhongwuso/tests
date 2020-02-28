package cust.aowei.jwtstudy.interceptor;

import cust.aowei.jwtstudy.exception.CommonException;
import cust.aowei.jwtstudy.model.ResultCode;
import cust.aowei.jwtstudy.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 *  继承HandlerInterceptorAdapter
 *  preHandle: 进入到控制器方法之前执行的内容
 *      boolean：
 *          true: 可以继续执行控制器方法
 *          false: 拦截
 *  postHandler: 执行控制器方法之后执行的内容
 *  afterHandler: 响应结束之前执行的内容
 *
 *  简化获取token数据的代码编写
 *      统一的用户权限校验（是否登录）
 *  判断用户是否具有当前访问接口的权限
 * @author aowei
 */

@Component
public class JwtInterceptor extends HandlerInterceptorAdapter {

    /**
     * 通过拦截器获取token数据
     * 从token中解析获取claims
     * 将claims绑定到request域中
     */
    @Autowired
    JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 通过request获取请求token信息
        String authorization = request.getHeader("Authorization");
        // 判断请求头信息是否为空，或是否以Bearer 开头
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer")){
            // 获取token数据
            String token = authorization.replace("Bearer","");
            // 解析token获取claims
            Claims claims = jwtUtils.parseJwt(token);
            if(claims != null){
                // 通过claims获取到当前用户的可访问api权限字符串
                String apis = (String) claims.get("roles");
                // 通过handler
                HandlerMethod h = (HandlerMethod) handler;
                // 获取接口上的requestmapping注解
                RequestMapping annotation = h.getMethodAnnotation(RequestMapping.class);
                // 获取当前请求接口中的name属性
                String name = annotation.name();
                if(apis.contains(name)){
                    request.setAttribute("userClaims",claims);
                    return true;
                }else{
                    throw new CommonException(ResultCode.UNAUTHORISE);
                }

            }
        }
        throw new CommonException(ResultCode.UNAUTHENTICATED);
    }
}
