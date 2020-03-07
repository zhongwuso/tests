# JWT-study

## 介绍

SpringBoot 集成 JWT 实现 token 鉴权

教学视频地址：https://www.bilibili.com/video/av75572951?p=108

# SpringBoot 集成 JWT 实现 token 鉴权
**完整项目地址：https://gitee.com/aoxiaobao/JWT-study.git**
***
## pom依赖
* 首先当然是引入pom依赖
```javascript
<dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.0</version>
        </dependency>
```
## 一个简单的小例子 
***
* 创建 **token**
	* 通过使用 **Jwts.builder()**
	* 在 **token** 添加一些简单的用户信息
	* 选择一个加密算法
	* 就可以创建出一个 **token**
* 解析 **token**
	* 通过 **Jwts.parser()**
	* 获取其中加密的用户信息
```javascript
public class TestJwt {
    public static void main(String[] args) {
        System.out.println(createJwt());

        parseJwt(createJwt());
    }
    public static String createJwt(){
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId("888888") // 添加id
                .setSubject("藏冰") // 添加用户名
                .setIssuedAt(new Date()) // 添加当前时间
                .signWith(SignatureAlgorithm.HS256,"aowei"); // 设置加密算法，密钥
        String token = jwtBuilder.compact(); // 获取token
        return token;
    }
    public static void parseJwt(String token){
        Claims claims = Jwts.parser()
                .setSigningKey("aowei")
                .parseClaimsJws(token).getBody();

        System.out.println(claims.getId());
        System.out.println(claims.getSubject());
        System.out.println(claims.getIssuedAt());
    }
}
```
## JwtUtils 工具类
***
* 将上面的简单例子进一步完善，
* 把 **私钥** 和 **签名**的失效时间，单独提取出来
* 还可以在 **token** 中添加自定义的 **Map** 格式数据
* 最后添加上自定义异常的异常处理
```javascript

public class JwtUtils {
    // 签名私钥
    public static final String AUTH_HEADER_KEY = "Authorization";
    // 签名的失效时间
    private static final long TTL = 10000 ;

    private static Logger log = LoggerFactory.getLogger(JwtUtils.class);
    /**
     * 设置认证token
     * id:登录用户id
     * subject:登录用户名
     */

    public static String createJwt(String id, String name, Map<String,Object> map) throws CustomException {
        // 设置失效时间
        long now = System.currentTimeMillis(); // 当前毫秒数
        long exp = now + TTL;

        // 创建jwtBuilder
        String token = null;
        try {
            JwtBuilder jwtBuilder = Jwts.builder()
                    .setId(id) // 添加id
                    .setSubject(name) // 添加用户名
                    .setIssuedAt(new Date()) // 添加当前时间
                    .signWith(SignatureAlgorithm.HS256,AUTH_HEADER_KEY); // 设置加密算法，密钥

            // 根据map设置claims
            for (Map.Entry<String,Object> entry : map.entrySet()){
                jwtBuilder.claim(entry.getKey(),entry.getValue());
            }

            // 指定失效时间
            jwtBuilder.setExpiration(new Date(exp));

            // 创建token
            token = jwtBuilder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new CustomException(ResultCode.PERMISSION_SIGNATURE_ERROR);
        }
        return token;
    }

    /**
     * 解析token字符串，获取clamis
     */
    public static Claims parseJwt(String token) throws CustomException {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(AUTH_HEADER_KEY)
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("===== Token过期 =====", e);
            throw new CustomException(ResultCode.PERMISSION_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error("===== token解析异常 =====", e);
            throw new CustomException(ResultCode.PERMISSION_TOKEN_INVALID);
        }
    }
}
```
## 自定义JWT拦截器
***
* 创建一个 **token** 拦截器

*  继承 **HandlerInterceptorAdapter**
* 有三个常用的方法，这里只需要重写 **preHandle** 方法即可
* 重写 **preHandle**方法（进入到控制器方法之前执行的内容）
	* 返回值是 boolean类型
	* true: 可以继续执行控制器方法
	* false: 拦截请求
* postHandler（执行控制器方法之后执行的内容）
* afterHandler（响应结束之前执行的内容）

* 这里拦截器要实现的功能有
	* 简化获取token数据的代码编写
	* 统一的用户权限校验（是否登录）
	* 判断用户是否具有当前访问接口的权限
```javascript
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
@Slf4j
public class JwtInterceptor extends HandlerInterceptorAdapter {

    /**
     * 通过拦截器获取token数据
     * 从token中解析获取claims
     * 将claims绑定到request域中
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 忽略带JwtIgnore注解的请求, 不做后续token认证校验
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            JwtIgnore jwtIgnore = handlerMethod.getMethodAnnotation(JwtIgnore.class);
            if (jwtIgnore != null) {
                return true;
            }
        }

        // 通过request获取请求token信息
        String authorization = request.getHeader("Authorization");
        // 判断请求头信息是否为空，或是否以Bearer 开头
        if(!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer")){
            // 获取token数据
            String token = authorization.replace("Bearer","");
            // 解析token获取claims
            Claims claims = JwtUtils.parseJwt(token);
            if(claims != null){
                // 通过claims获取到当前用户的可访问api权限字符串
                String apis = (String) claims.get("roles");
                // 通过handler
                String name = null;
                if (handler instanceof HandlerMethod) {
                    HandlerMethod h = (HandlerMethod) handler;
                    // 获取接口上的requestmapping注解
                    RequestMapping annotation = h.getMethodAnnotation(RequestMapping.class);
                    // 获取当前请求接口中的name属性
                    name = annotation.name();
                }else {
                    throw new CustomException(ResultCode.INTERFACE_ADDRESS_INVALID);
                }
                if(apis.contains(name)){
                    request.setAttribute("userClaims",claims);
                    return true;
                }else{
                    throw new CustomException(ResultCode.PERMISSION_UNAUTHORISE);
                }
            }
        }
        throw new CustomException(ResultCode.USER_NOT_LOGGED_IN);
    }
}
```
## 配置拦截器
***
* 拦截器写好之后，还不能真正起到作用
* 这里需要配置一下 JWT 拦截器
* 实现 **WebMvcConfigurer** 接口
* 用 **addPathPatterns()** 方法拦截请求
* /**表示拦截所有的请求
* 可以在 **excludePathPatterns()** 方法中指明不需要拦截的路径
```javascript
@Configuration
public class JwtConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器的配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        // 添加自定义拦截器
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**");
    }
    /**
     * 跨域支持
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS", "HEAD")
                .maxAge(3600 * 24);
    }
}
```
## 自定义注解
***
* 通过自定义注解 **@JwtIgnore**
* 实现放行某些接口（不拦截请求）
```javascript
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtIgnore {
//    String value() default "JWT";
}
```
## 代码测试
***
* 将用户的角色也添加到 **token** 中
* 在拦截器中解析 **token** 获得用户角色
* 再从 **handler** 中获取请求的 **RequestMapping**注解的**name**属性
* 可以在**name**属性里，写上访问该接口所需的权限名称
* 比较 用户角色和 **name** 属性，就可以判断出用户是否有访问该接口的权限
```javascript
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

    // 测试需要权限的url（需要登录且拥有all-admin的权限）
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
```
