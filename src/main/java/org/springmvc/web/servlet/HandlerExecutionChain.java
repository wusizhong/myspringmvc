package org.springmvc.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class HandlerExecutionChain {

    private Object handler;

    private List<HandlerInterceptor> interceptorList;

    private int interceptorIndex = -1;

    public HandlerExecutionChain() {
    }

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptorList, int interceptorIndex) {
        this.handler = handler;
        this.interceptorList = interceptorList;
        this.interceptorIndex = interceptorIndex;
    }

    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }

    public List<HandlerInterceptor> getInterceptorList() {
        return interceptorList;
    }

    public void setInterceptorList(List<HandlerInterceptor> interceptorList) {
        this.interceptorList = interceptorList;
    }

    public int getInterceptorIndex() {
        return interceptorIndex;
    }

    public void setInterceptorIndex(int interceptorIndex) {
        this.interceptorIndex = interceptorIndex;
    }

    public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) {
        for (int i = 0; i < interceptorList.size(); i++) {
            HandlerInterceptor interceptor = interceptorList.get(i);
            try {
                if (!interceptor.preHandle(request, response, this.handler)) {
                    // 如果返回false，表示拦截器已经处理了，不需要继续执行
                    triggerAfterCompletion(request, response, null);
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.interceptorIndex = i;
        }
        return true;
    }

    public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) {
        for (int i = interceptorList.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptorList.get(i);
            try {
                interceptor.postHandle(request, response, this.handler, mv);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler) {
        for (int i = interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptorList.get(i);
            try {
                interceptor.afterCompletion(request, response, handler);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
