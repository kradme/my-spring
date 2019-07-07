package cn.lang.spring.framework.webmvc.servlet;

import cn.lang.spring.demo.action.DemoAction;
import cn.lang.spring.framework.annotation.Autowired;
import cn.lang.spring.framework.annotation.Controller;
import cn.lang.spring.framework.annotation.Service;
import cn.lang.spring.framework.context.MyApplicationContext;

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

    private final String LOCATION="contextConfigLocation";
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

        MyApplicationContext myApplicationContext = new MyApplicationContext(config.getInitParameter(LOCATION));
        DemoAction demoAction = (DemoAction) myApplicationContext.getBean("demoAction");
    }

}
