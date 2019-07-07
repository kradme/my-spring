package cn.lang.spring.framework.config;

import org.springframework.beans.BeansException;

public class BeanPostProcesser {
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
