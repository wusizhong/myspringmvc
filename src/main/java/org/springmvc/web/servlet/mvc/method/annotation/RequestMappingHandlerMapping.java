package org.springmvc.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import org.springmvc.web.context.WebApplicationContext;
import org.springmvc.web.method.HandlerMethod;
import org.springmvc.web.method.RequestMappingInfo;
import org.springmvc.web.servlet.HandlerExecutionChain;
import org.springmvc.web.servlet.HandlerInterceptor;
import org.springmvc.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Map;

import static org.springmvc.web.constant.Const.INTERCEPTORS;
import static org.springmvc.web.constant.Const.WEB_APPLICATION_CONTEXT;

public class RequestMappingHandlerMapping implements HandlerMapping {

    /**
     * key: 请求路径和请求方式
     * value: 请求对应要调用的方法
     */
    private Map<RequestMappingInfo, HandlerMethod> map;

    public RequestMappingHandlerMapping(Map<RequestMappingInfo, HandlerMethod> map) {
        this.map = map;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        HandlerExecutionChain handlerExecutionChain = new HandlerExecutionChain();
        RequestMappingInfo requestMappingInfo = new RequestMappingInfo();
        requestMappingInfo.setRequestUri(request.getServletPath());
        requestMappingInfo.setRequestMethod(request.getMethod());
        //通过请求路径和请求方式获取对应的处理器方法
        handlerExecutionChain.setHandler(map.get(requestMappingInfo));
        //获取所有拦截器
        WebApplicationContext webApplicationContext = (WebApplicationContext) request.getServletContext().getAttribute(WEB_APPLICATION_CONTEXT);
        List<HandlerInterceptor> handlerInterceptorList = (List<HandlerInterceptor>) webApplicationContext.getBean(INTERCEPTORS);
        handlerExecutionChain.setInterceptorList(handlerInterceptorList);
        return handlerExecutionChain;
    }
}
