package org.springmvc.web.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springmvc.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Locale;

import static org.springmvc.web.constant.Const.*;

public class DispatcherServlet extends HttpServlet {

    private ViewResolver viewResolver;

    private HandlerMapping handlerMapping;

    private HandlerAdapter handlerAdapter;

    @Override
    public void init() throws ServletException {
        //获取web.xml配置文件中的contextConfigLocation的值
        ServletConfig servletConfig = this.getServletConfig();
        String contextConfigLocation = servletConfig.getInitParameter(CONTEXT_CONFIG_LOCATION);
        String contextConfigLocationPath = null;
        if (contextConfigLocation.trim().startsWith(PREFIX_CLASSPATH)) {
            //从类路径找到mvc配置文件
            String substring = contextConfigLocation.substring(PREFIX_CLASSPATH.length());
            String path = Thread.currentThread().getContextClassLoader()
                    .getResource(substring).getPath();
            contextConfigLocationPath = URLDecoder.decode(path, Charset.defaultCharset());
        }

        //初始化容器
        ServletContext servletContext = this.getServletContext();
        WebApplicationContext webApplicationContext = new WebApplicationContext(contextConfigLocationPath, servletContext);
        servletContext.setAttribute(WEB_APPLICATION_CONTEXT, webApplicationContext);

        //初始化HandlerMapping
        this.handlerMapping = (HandlerMapping) webApplicationContext.getBean(HANDLER_MAPPING);
        //初始化HandlerAdapter
        this.handlerAdapter = (HandlerAdapter) webApplicationContext.getBean(HANDLER_ADAPTER);
        //初始化ViewResolver
        this.viewResolver = (ViewResolver) webApplicationContext.getBean(VIEW_RESOLVER);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            doDispatcher(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doDispatcher(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //根据请求路径和请求方式获取HandlerExecutionChain
        HandlerExecutionChain mappingHandler = this.handlerMapping.getHandler(request);
        //根据HandlerExecutionChain获取HandlerAdapter
        HandlerAdapter ha = this.handlerAdapter;
        //执行拦截器中的preHandler方法
        if (!mappingHandler.applyPreHandle(request, response)) {
            return;
        }
        //执行HandlerAdapter中的handle方法
        ModelAndView mv = ha.handle(request, response, mappingHandler.getHandler());
        //执行拦截器中的postHandler方法
        mappingHandler.applyPostHandle(request, response, mv);
        //通过视图解析器渲染视图
        View view = this.viewResolver.resolveViewName(mv.getView().toString());
        view.render(mv.getModelMap(), request, response);
        //执行拦截器中的afterCompletion方法
        mappingHandler.triggerAfterCompletion(request, response, null);
    }
}
