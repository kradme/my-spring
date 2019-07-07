package cn.lang.spring.framework.context;

import cn.lang.spring.framework.annotation.Autowired;
import cn.lang.spring.framework.annotation.Controller;
import cn.lang.spring.framework.annotation.Service;
import cn.lang.spring.framework.beans.BeanDefinition;
import cn.lang.spring.framework.beans.BeanWrapper;
import cn.lang.spring.framework.config.BeanPostProcesser;
import cn.lang.spring.framework.core.BeanFactory;
import cn.lang.spring.framework.support.BeanDefinitionReader;

import java.lang.reflect.Field;
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext implements BeanFactory {

    private String[] configLocations;
    private BeanDefinitionReader beanDefinitionReader;
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private Map<String, Object> beanCacheMap = new ConcurrentHashMap<>();
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

    public MyApplicationContext(String...locations) {
        this.configLocations = locations;
        this.refresh();
    }

    private void refresh() {
        //定位
        beanDefinitionReader = new BeanDefinitionReader(configLocations);
        //加载
        List<String> beanDefinitions = beanDefinitionReader.loadBeanDefinitions();
        //注册
        doRegisty(beanDefinitions);
        //依赖注入
        doAutowired();

    }

    private void doAutowired() {
        this.beanDefinitionMap.entrySet().forEach(entry->{
            BeanDefinition beanDefinition = entry.getValue();

            if (!beanDefinition.isLazyInit()){
                getBean(entry.getKey());
            }
        });
    }

    public void populateBean(String beanName, Object instance){
        Class<?> beanClass = instance.getClass();
        if (!beanClass.isAnnotationPresent(Service.class) || !beanClass.isAnnotationPresent(Controller.class)) return;

        Field[] declaredFields = beanClass.getDeclaredFields();
        if (declaredFields==null || declaredFields.length==0) return;

        Arrays.stream(declaredFields).forEach(field -> {
            if (field.isAnnotationPresent(Autowired.class) ){
                Autowired autowired = field.getAnnotation(Autowired.class);
                String autowiredName = field.getName();
                if (autowired.value()!=null && autowired.value().trim().length()>0){
                    autowiredName = autowired.value();
                }
                Object autowiredInstance = this.beanWrapperMap.get(autowiredName).getWrappedInstance();
                field.setAccessible(true);
                try {
                    field.set(instance, autowiredInstance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doRegisty(List<String> beanDefinitions) {
        if (beanDefinitions.isEmpty()) return;
        for (String className : beanDefinitions){
            try {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) continue;
                BeanDefinition beanDefinition = beanDefinitionReader.registryBean(className);
                beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);

                Class<?>[] interfaces = beanClass.getInterfaces();
                Arrays.stream(interfaces).forEach(intface->{
                    beanDefinitionMap.put(intface.getName(), beanDefinition);
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        String className = beanDefinition.getBeanClassName();

        try{
            BeanPostProcesser beanPostProcesser = new BeanPostProcesser();

            Object bean = this.instantionBean(beanDefinition);
            if (bean==null) return null;
            //前置监听器
            beanPostProcesser.postProcessBeforeInitialization(bean, beanName);
            BeanWrapper beanWrapper = new BeanWrapper(bean);
            beanWrapper.setBeanPostProcesser(beanPostProcesser);
            this.beanWrapperMap.put(beanName, beanWrapper);
            //后置监听器
            beanPostProcesser.postProcessAfterInitialization(bean, beanName);

            populateBean(beanName, bean);

            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    private Object instantionBean(BeanDefinition beanDefinition){
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {

            if (beanDefinition.isSingleton()){
                if (this.beanCacheMap.containsKey(className)){
                    instance= beanCacheMap.get(className);
                }else {
                    instance = Class.forName(className).newInstance();
                    beanCacheMap.put(className, instance);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return instance;
    }
}
