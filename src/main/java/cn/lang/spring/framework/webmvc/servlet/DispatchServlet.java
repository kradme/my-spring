package cn.lang.spring.framework.webmvc.servlet;

import cn.lang.spring.framework.annotation.Autowired;
import cn.lang.spring.framework.annotation.Controller;
import cn.lang.spring.framework.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchServlet extends HttpServlet {
    private Properties contextConfig = new Properties();
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
    private List<String> beanNames = new ArrayList<>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) {

        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //加载
        doScanner(contextConfig.getProperty("scanPackage"));

        //注册
        doRegistry();

        //注入
        doAutowired();

        //mvc--handlerMapping
        initHandlerMapping();
    }

    private void initHandlerMapping() {
    }

    private void doRegistry() {
        if (beanNames==null || beanNames.isEmpty()) return;
        beanNames.stream().forEach(className->{
            try {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(Controller.class)){

                    beanMap.put(this.lowerFirstCase(clazz.getSimpleName()), clazz.newInstance());
                }else if (clazz.isAnnotationPresent(Service.class)){
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = this.lowerFirstCase(clazz.getSimpleName());
                    if (service.value()!=null && !service.value().trim().equals("")){
                        beanName = service.value();
                    }

                    beanMap.put(beanName, clazz.newInstance());

                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class c : interfaces){
                        beanMap.put(lowerFirstCase(c.getSimpleName()), clazz.newInstance());
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }
    private String lowerFirstCase(String str){
        if (str==null) return null;
        return str.substring(0,1).toLowerCase()+str.substring(1);
    }

    private void doAutowired() {
        if (beanMap.isEmpty()) return;
        beanMap.entrySet().stream().forEach(entry->{
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            Arrays.stream(fields).forEach(field -> {
                if (field.isAnnotationPresent(Autowired.class)){
                    String beanName = field.getName();
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String value = autowired.value();
                    if (value!=null && !value.trim().equals("")){
                        beanName = value;
                    }
                    Object o = beanMap.get(beanName);
                    if (o==null) throw new RuntimeException("未知的bean="+beanName);
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }


    private void doScanner(String packageName) {

        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());
        for (File f : classDir.listFiles()){
            if (f.isDirectory()){
                this.doScanner(packageName+"."+f.getName());
            }else {
                beanNames.add(packageName+"."+f.getName().replace(".class",""));
            }
        }
    }

    private void doLoadConfig(String location){

        //通过reader查找、定位配置
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:","/"))) {

            contextConfig.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
