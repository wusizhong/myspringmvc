package org.springmvc.web.servlet.view;

import org.springmvc.web.servlet.View;
import org.springmvc.web.servlet.ViewResolver;

public class InternalResourceViewResolver implements ViewResolver {

    private String prefix;
    private String suffix;

    public InternalResourceViewResolver() {
    }

    public InternalResourceViewResolver(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 根据视图名称解析为视图对象
     */
    @Override
    public View resolveViewName(String viewName) {
        return new InternalResourceView("text/html;charset=UTF-8", prefix + viewName + suffix);
    }
}
