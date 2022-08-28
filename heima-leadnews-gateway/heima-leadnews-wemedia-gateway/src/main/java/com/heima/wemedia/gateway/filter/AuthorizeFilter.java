package com.heima.wemedia.gateway.filter;


import com.heima.wemedia.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断是否是登录
        if(request.getURI().getPath().contains("/login")){
            //放行
            return chain.filter(exchange);
        }

        //3.获取token
        String token = request.getHeaders().getFirst("token");

        //4.判断token是否存在
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if(result == 1 || result  == 2){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        //补充自媒体发布时候需要获取的用户id,将该用户id放入请求头,在文章微服务处进行拦截器拦截,取到用户id并存入ThreadLocal中
            //创建token的时候,封装的就是用户id,根据token工具类中名称获取
            Object userId = claimsBody.get("id");
            //将userId存入请求头中
            ServerHttpRequest httpRequest = request.mutate().header("userId", userId.toString()).build();
            //此时产生的serverHttpRequest与request没有任何关系，如果就这么往下走，下面的还是request，而且不会携带加入的请求头，此时需要重置请求，即把从哪取出来的request，再设置回去，取到的request是通过exchange
            //exchange上下文对象只有一个，因此不不需要build
            exchange.mutate().request(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置  值越小  优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
