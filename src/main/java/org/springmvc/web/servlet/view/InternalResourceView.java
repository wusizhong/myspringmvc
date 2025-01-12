package org.springmvc.web.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springmvc.web.servlet.View;

import java.util.Map;

public class InternalResourceView implements View {

    private String contentType;
    private String path;

    public InternalResourceView() {
    }

    public InternalResourceView(String contentType, String path) {
        this.contentType = contentType;
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置响应内容类型
        response.setContentType(contentType);
        // 将数据存储到request域中
        model.forEach(request::setAttribute);
        // 转发到指定视图
        request.getRequestDispatcher(path).forward(request, response);
    }
}
