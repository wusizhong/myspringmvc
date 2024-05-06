package org.springmvc.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 处理器适配器
 */
public interface HandlerAdapter {
    /**
     * 用来真正调用处理器方法，返回ModelAndView对象实例
     */
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
}
