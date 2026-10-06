package com.warehouse.interceptor;

import com.warehouse.service.PermissionService;
import com.warehouse.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 * 实现基于permission表的权限校验
 * 
 * @author 毛煜祺,聂智天,钟昌盛
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermissionService permissionService;

    @Value("${jwt.header}")
    private String header;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取Token
        String token = request.getHeader(header);
        
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
            return false;
        }

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期，请重新登录\"}");
            return false;
        }

        // 将用户信息存入请求属性
        Integer userId = jwtUtil.getUserIdFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);

        // 根据请求URI和HTTP方法映射到权限名称
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String permissionName = getPermissionName(requestURI, method);

        // 如果找到了对应的权限名称，进行权限检查
        if (permissionName != null) {
            if (!permissionService.hasPermission(role, permissionName)) {
                response.setStatus(403); // 403 Forbidden
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"无权访问此接口\"}");
                return false;
            }
        }

        return true;
    }

    /**
     * 根据请求URI和HTTP方法映射到权限名称
     * 
     * @param requestURI 请求URI
     * @param method HTTP方法
     * @return 权限名称，如果不需要权限检查则返回null
     */
    private String getPermissionName(String requestURI, String method) {
        // 商品管理权限
        if (requestURI.startsWith("/product")) {
            if (requestURI.contains("/add") && "POST".equals(method)) {
                return "商品新增";
            } else if (requestURI.contains("/update") && "PUT".equals(method)) {
                return "商品修改";
            } else if (requestURI.contains("/delete") && "DELETE".equals(method)) {
                return "商品删除";
            } else if (requestURI.contains("/get") || requestURI.contains("/list") || 
                       requestURI.contains("/page") || requestURI.contains("/search")) {
                return "商品查询";
            }
        }
        
        // 仓库管理权限
        if (requestURI.startsWith("/warehouse")) {
            if (requestURI.contains("/add") && "POST".equals(method)) {
                return "仓库新增";
            } else if (requestURI.contains("/update") && "PUT".equals(method)) {
                return "仓库修改";
            } else if (requestURI.contains("/delete") && "DELETE".equals(method)) {
                return "仓库删除";
            } else if (requestURI.contains("/get") || requestURI.contains("/list") || 
                       requestURI.contains("/page")) {
                return "仓库查询";
            }
        }
        
        // 货架管理权限
        if (requestURI.startsWith("/shelf")) {
            if (requestURI.contains("/add") && "POST".equals(method)) {
                return "货架新增";
            } else if (requestURI.contains("/update") && "PUT".equals(method)) {
                return "货架修改";
            } else if (requestURI.contains("/delete") && "DELETE".equals(method)) {
                return "货架删除";
            } else if (requestURI.contains("/get") || requestURI.contains("/list") || 
                       requestURI.contains("/page") || requestURI.contains("/warehouse")) {
                return "货架查询";
            }
        }
        
        // 用户管理权限
        if (requestURI.startsWith("/user")) {
            if (requestURI.contains("/add") && "POST".equals(method)) {
                return "用户新增";
            } else if (requestURI.contains("/update") && "PUT".equals(method)) {
                return "用户修改";
            } else if (requestURI.contains("/delete") && "DELETE".equals(method)) {
                return "用户删除";
            } else if (requestURI.contains("/get") || requestURI.contains("/list") || 
                       requestURI.contains("/page") || requestURI.contains("/role")) {
                return "用户查询";
            }
        }

        // 操作日志查询权限
        if (requestURI.startsWith("/operation-log") || requestURI.startsWith("/log")) {
            return "操作日志查询";
        }

        // 库存管理权限
        if (requestURI.startsWith("/inventory")) {
            // 库存统计相关接口（利用率、格口状态等）
            if (requestURI.contains("/utilization") || requestURI.contains("/slots") || 
                requestURI.contains("/empty-slots") || requestURI.contains("/check-slot-conflict")) {
                return "库存统计";
            }
            // 库存查询接口
            if (requestURI.contains("/list") || requestURI.contains("/slot/quantity")) {
                return "库存查询";
            }
        }

        // 入库操作权限
        if (requestURI.startsWith("/stock-in")) {
            if (requestURI.contains("/add") && "POST".equals(method)) {
                return "商品入库";
            }
        }

        // 如果找不到对应的权限，返回null（表示不需要权限检查或权限未定义）
        return null;
    }
}

