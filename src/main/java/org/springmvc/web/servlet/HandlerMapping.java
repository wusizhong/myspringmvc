package org.springmvc.web.servlet;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 处理器映射器
 */
public interface HandlerMapping {

    /**
     * 根据请求路径返回处理器执行链对象
     */
    HandlerExecutionChain getHandler(HttpServletRequest request);
}
