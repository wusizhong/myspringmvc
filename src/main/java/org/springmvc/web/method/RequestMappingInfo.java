package org.springmvc.web.method;

import java.util.Objects;

public class RequestMappingInfo {
    private String requestUri;
    private String requestMethod;

    public RequestMappingInfo() {
    }

    public RequestMappingInfo(String requestUri, String requestMethod) {
        this.requestUri = requestUri;
        this.requestMethod = requestMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestMappingInfo that = (RequestMappingInfo) o;
        return Objects.equals(requestUri, that.requestUri) && Objects.equals(requestMethod, that.requestMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestUri, requestMethod);
    }
}
