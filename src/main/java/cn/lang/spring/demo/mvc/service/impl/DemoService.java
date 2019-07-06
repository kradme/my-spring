package cn.lang.spring.demo.mvc.service.impl;


import cn.lang.spring.demo.mvc.service.IDemoService;
import cn.lang.spring.framework.annotation.Service;

@Service
public class DemoService implements IDemoService {
    @Override
    public String get(String name) {
        return "name : "+name;
    }
}
