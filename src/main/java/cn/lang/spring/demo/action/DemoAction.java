package cn.lang.spring.demo.action;


import cn.lang.spring.demo.mvc.service.IDemoService;
import cn.lang.spring.framework.annotation.Autowired;
import cn.lang.spring.framework.annotation.Controller;
import cn.lang.spring.framework.annotation.RequestMapping;
import cn.lang.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class DemoAction {
    @Autowired
    private IDemoService demoService;

    @RequestMapping("/query.json")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name){
        String result = demoService.get(name);
        System.out.println(result);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
