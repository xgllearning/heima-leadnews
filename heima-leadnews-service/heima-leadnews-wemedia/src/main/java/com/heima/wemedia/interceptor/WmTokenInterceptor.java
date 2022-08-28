package com.heima.wemedia.interceptor;


import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//当加入网关之后，在网关设置好过滤器之后，就不需要此拦截器再解析不合法的了，因为此时进来的都是合法的
//拦截器，前置拦截进行处理
public class WmTokenInterceptor implements HandlerInterceptor {


    //处理请求中的数据，可以得到header中的用户信息，并且存入当前线程
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头中携带的信息
        String userId = request.getHeader("userId");
        if (StringUtils.isNotBlank(userId)){
            //创建需要存入ThreadLocal的对象wmUser
            WmUser wmUser = new WmUser();
            wmUser.setId(Integer.valueOf(userId));
            WmThreadLocalUtil.setUser(wmUser);
        }
        return true;
    }

    //清理线程数据需要在after中
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtil.clear();
    }


    //post拦截器只处理成功的请求，不成功的请求不会进入而是直接进入了after拦截器
}
