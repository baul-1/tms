package com.icode.toonmanger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
public class GetParamResolver  implements HandlerMethodArgumentResolver {

    @Autowired
    protected RedisTemplate<String,Object> redis;

    @Override
    public boolean supportsParameter(MethodParameter parameter){
        return parameter.getParameterAnnotation(GetParam.class) != null;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        CParam param = new CParam(redis);

        MultiValueMap map = request.getQueryParams();


        Set<String> keys = map.keySet();
        for(String key :keys){
            List<String> paramValues = (List<String>) map.get(key);
            Object result = null;
            if (paramValues != null) {
                result = (paramValues.size() == 1 ? paramValues.get(0) : paramValues);
            }
            param.put(key,result);
        }

        return Mono.just(param);
    }
}
