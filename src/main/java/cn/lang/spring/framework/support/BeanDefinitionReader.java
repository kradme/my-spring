package cn.lang.spring.framework.support;

import cn.lang.spring.framework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class BeanDefinitionReader {

    private Properties config = new Properties();
    private List<String> registerBeanClasses = new ArrayList<>();

    public BeanDefinitionReader(String...locations) {
        if (locations==null) return;
        Arrays.stream(locations).forEach(location->{
            //通过reader查找、定位配置
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:","/"))) {

                config.load(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.doScanner(config.getProperty("scanPackage"));

    }

    private void doScanner(String packageName) {

        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());
        for (File f : classDir.listFiles()){
            if (f.isDirectory()){
                this.doScanner(packageName+"."+f.getName());
            }else {
                registerBeanClasses.add(packageName+"."+f.getName().replace(".class",""));
            }
        }
    }

    public List<String> loadBeanDefinitions(){
        return this.registerBeanClasses;
    }

    public BeanDefinition registryBean(String className){
        if (registerBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(StringUtil.lowFirst(className.substring(className.lastIndexOf(".")+1)));
            return beanDefinition;
        }
        return null;
    }

    public Properties getConfig(){
        return this.config;
    }

}
