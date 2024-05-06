package org.springmvc.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springmvc.ui.ModelMap;
import org.springmvc.web.method.HandlerMethod;
import org.springmvc.web.servlet.HandlerAdapter;
import org.springmvc.web.servlet.ModelAndView;

import java.lang.reflect.Method;

public class RequestMappingHandlerAdapter implements HandlerAdapter {

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //调用handler方法
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Object controller = handlerMethod.getHandler();
        Method method = handlerMethod.getMethod();
        ModelMap modelMap = new ModelMap();
        String viewName = (String) method.invoke(controller, modelMap);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(viewName);
        modelAndView.setModelMap(modelMap);
        return modelAndView;
    }
}
