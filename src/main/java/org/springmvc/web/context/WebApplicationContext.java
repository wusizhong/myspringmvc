package org.springmvc.web.context;

import jakarta.servlet.ServletContext;
import org.springmvc.context.ApplicationContext;

public class WebApplicationContext extends ApplicationContext {

    private String configPath;

    private ServletContext servletContext;


    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public WebApplicationContext(String contextConfigLocationPath, ServletContext servletContext) {
        super(contextConfigLocationPath);
        this.servletContext = servletContext;
    }
}
