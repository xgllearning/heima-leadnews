package com.heima.app.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.app.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //    //把不需要校验的连接放到list集合中，通过@value注入
    //    @Value("${gateway.excludedUrls}")
    //    private List<String> excludeUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //从filter完成权限校验
        //1.获取当前的请求链接
        String path = exchange.getRequest().getURI().getPath();
        ServerHttpResponse response = exchange.getResponse();
        System.out.println("url:" + path);
        //2.判断是否是登录，登录则放行，不需要校验
        if (path.contains("/login")) {
            return chain.filter(exchange);//放行，向后执行
        }

        //3.不是登录，获取token,判断是否为空(有时候token前面携带某些参数，后台系统页面发送的token以"Bearer "开头，需要处理)
        //标准写法，token的请求名是什么需要跟前端确认
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            //为空返回权限不够，返回401,setComplete是结束
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        try {
            //4.调用AppJwtUtil工具类解析token,如果解析失败会抛出异常，如果解析成功拿到的是携带的内容
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //5.是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //6.放行
        return chain.filter(exchange);
        }

        //响应错误数据的方法，目的是将map集合转换成json返回
        private Mono<Void> responseError (ServerHttpResponse response, Map < String, Object > responseData){
            // 将信息转换为 JSON
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] data = new byte[0];
            try {
                data = objectMapper.writeValueAsBytes(responseData);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            // 输出错误信息到页面
            DataBuffer buffer = response.bufferFactory().wrap(data);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        }

        @Override
        public int getOrder () {
            return Ordered.LOWEST_PRECEDENCE;//低级别过滤
        }


    }
