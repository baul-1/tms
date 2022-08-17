package com.icode.toonmanger.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
class PostParamResolver implements HandlerMethodArgumentResolver {

    @Autowired
    protected RedisTemplate<String,Object> redis;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PostParam.class) != null;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {

        return exchange.getFormData().map(formData -> {
            CParam param = new CParam(redis);
            param.setByRequestParam(formData.toSingleValueMap());
            return param;
        });

    }
}