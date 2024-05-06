package org.springmvc.context;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springmvc.stereotype.Controller;
import org.springmvc.web.bind.annotation.RequestMapping;
import org.springmvc.web.method.HandlerMethod;
import org.springmvc.web.method.RequestMappingInfo;
import org.springmvc.web.servlet.HandlerAdapter;
import org.springmvc.web.servlet.HandlerInterceptor;
import org.springmvc.web.servlet.HandlerMapping;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springmvc.web.constant.Const.*;

public class ApplicationContext {

    private Map<String, Object> beanMap = new HashMap<>();

    public ApplicationContext(String configPath) {
        try {
            //解析xml配置文件
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(configPath));
            //获取包扫描标签
            Element componentScanElement = (Element) document.selectSingleNode("/beans/component-scan");
            Map<RequestMappingInfo, HandlerMethod> map = componentScan(componentScanElement);

            Element viewResolverElement = (Element) document.selectSingleNode("/beans/bean");
            createViewResolver(viewResolverElement);

            Element interceptorsElement = (Element) document.selectSingleNode("/beans/interceptors");
            createInterceptors(interceptorsElement);

            createHandlerMapping(DEFAULT_PACKAGE, map);

            createHandlerAdapter(DEFAULT_PACKAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createHandlerAdapter(String packageName) {
        String packagePath = packageName.replace(".", "/");
        String absolutePath = Thread.currentThread().getContextClassLoader().getResource(packagePath).getPath();
        //String absolutePath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File file = new File(absolutePath);
        File[] files = file.listFiles();
        for (File f : files) {
            String name = f.getName();
            String simpleName = name.substring(0, name.lastIndexOf("."));
            try {
                Class<?> clazz = Class.forName(packageName + "." + simpleName);
                //判断是否为HandlerAdapter的实现类
                if (HandlerAdapter.class.isAssignableFrom(clazz)) {
                    Object bean = clazz.newInstance();
                    beanMap.put(HANDLER_ADAPTER, bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createHandlerMapping(String packageName, Map<RequestMappingInfo, HandlerMethod> map) {
        String packagePath = packageName.replace(".", "/");
        String absolutePath = Thread.currentThread().getContextClassLoader().getResource(packagePath).getPath();
        //String absolutePath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File file = new File(absolutePath);
        File[] files = file.listFiles();
        for (File f : files) {
            String name = f.getName();
            String simpleName = name.substring(0, name.lastIndexOf("."));
            try {
                Class<?> clazz = Class.forName(packageName + "." + simpleName);
                //判断是否为HandlerMapping的实现类
                if (HandlerMapping.class.isAssignableFrom(clazz)) {
                    //Object bean = clazz.newInstance();
                    //调用有参构造方法创建HandlerMapping实例
                    Constructor<?> constructor = clazz.getDeclaredConstructor(Map.class);
                    Object bean = constructor.newInstance(map);
                    beanMap.put(HANDLER_MAPPING, bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createInterceptors(Element interceptorsElement) {
        List<HandlerInterceptor> handlerInterceptorList = new ArrayList<>();
        List<Element> elementList = interceptorsElement.elements("bean");
        for (Element element : elementList) {
            String className = element.attributeValue(CLASS_ATTRIBUTE);
            try {
                Class<?> clazz = Class.forName(className);
                Object bean = clazz.newInstance();
                handlerInterceptorList.add((HandlerInterceptor) bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        beanMap.put(INTERCEPTORS, handlerInterceptorList);
    }

    private void createViewResolver(Element viewResolverElement) {
        String className = viewResolverElement.attributeValue(CLASS_ATTRIBUTE);
        try {
            Class<?> clazz = Class.forName(className);
            Object bean = clazz.newInstance();
            List<Element> elementList = viewResolverElement.elements(PROPERTY);
            for (Element element : elementList) {
                String name = element.attributeValue(NAME);
                String value = element.attributeValue(VALUE);
                if (name.equals("prefix")) {
                    clazz.getMethod("setPrefix", String.class).invoke(bean, value);
                } else if (name.equals("suffix")) {
                    clazz.getMethod("setSuffix", String.class).invoke(bean, value);
                }
            }
            beanMap.put(VIEW_RESOLVER, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<RequestMappingInfo, HandlerMethod> componentScan(Element componentScanElement) {
        Map<RequestMappingInfo, HandlerMethod> map = new HashMap<>();
        String basePackage = componentScanElement.attributeValue(BASE_PACKAGE);
        String basePath = basePackage.replace(".", "/");
        String absolutePath = Thread.currentThread().getContextClassLoader().getResource(basePath).getPath();
        //String absolutePath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File file = new File(absolutePath);
        File[] files = file.listFiles();
        for (File f : files) {
            String name = f.getName();
            if (name.endsWith(SUFFIX_CLASS)) {
                String className = name.substring(0, name.lastIndexOf("."));
                try {
                    Class<?> clazz = Class.forName(basePackage + "." + className);
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        Object bean = clazz.newInstance();
                        beanMap.put(firstCharLowerCase(className), bean);
                        //创建这个bean中所有的handlerMethod对象
                        Method[] methods = clazz.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(RequestMapping.class)) {
                                //创建RequestMappingInfo
                                RequestMappingInfo requestMappingInfo = new RequestMappingInfo();
                                //获取RequestMapping注解中的请求路径和请求方式
                                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                                requestMappingInfo.setRequestUri(requestMapping.value()[0]);
                                requestMappingInfo.setRequestMethod(requestMapping.method().toString());
                                //创建HandlerMethod
                                HandlerMethod handlerMethod = new HandlerMethod();
                                handlerMethod.setHandler(bean);
                                handlerMethod.setMethod(method);
                                map.put(requestMappingInfo, handlerMethod);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    private String firstCharLowerCase(String className) {
        return (className.charAt(0) + "").toLowerCase() + className.substring(1);
    }

    public void setBean(String name, Object bean) {
        beanMap.put(name, bean);
    }

    public Object getBean(String name) {
        return beanMap.get(name);
    }
}
