package cn.lang.spring.framework.beans;

import cn.lang.spring.framework.config.BeanPostProcesser;
import cn.lang.spring.framework.core.FactoryBean;

public class BeanWrapper extends FactoryBean {
    private Object wrapperInstance;
    private Object originalInstance;
    private BeanPostProcesser beanPostProcesser;


    public BeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.originalInstance = instance;
    }

    public Object getWrappedInstance(){
        return wrapperInstance;
    }

    public Class getWarrperClass(){
        return wrapperInstance.getClass();
    }

    public BeanPostProcesser getBeanPostProcesser() {
        return beanPostProcesser;
    }

    public void setBeanPostProcesser(BeanPostProcesser beanPostProcesser) {
        this.beanPostProcesser = beanPostProcesser;
    }
}
