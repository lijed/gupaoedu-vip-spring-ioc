package com.gupaoedu.vip.spring.framework.beans.support;

import com.gupaoedu.vip.spring.framework.annotation.GPController;
import com.gupaoedu.vip.spring.framework.annotation.GPService;
import com.gupaoedu.vip.spring.framework.beans.config.GPBeanDefinition;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by Tom.
 */
public class GPBeanDefinitionReader {

    private Properties contextConfig = new Properties();

    //保存扫描的结果, 里面元素的类容是claasName
    private List<String> regitryBeanClasses = new ArrayList<String>();


    public GPBeanDefinitionReader(String... configLocations) {

        //加载配置文件
        doLoadConfig(configLocations[0]);

        //扫描配置文件中的配置的相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    public List<GPBeanDefinition> loadBeanDefinitions() {
        List<GPBeanDefinition> result = new ArrayList<GPBeanDefinition>();
        try {
            for (String className : regitryBeanClasses) {

                Class<?> beanClass = Class.forName(className);


                //保存类对应的ClassName（全类名）
                //还有beanName
                //1、默认是类名首字母小写

                //Default
                String beanName = toLowerFirstCase(beanClass.getSimpleName());

                //2、自定义
                //Customized BeanName
                String customizedBeanName = null;
                if (beanClass.isAnnotationPresent(GPService.class)) {
                    GPService gpService = beanClass.getAnnotation(GPService.class);
                    customizedBeanName = gpService.value();
                } else if (beanClass.isAnnotationPresent(GPController.class)){
                    GPController gpController = beanClass.getAnnotation(GPController.class);
                    customizedBeanName = gpController.value();
                }
                if (StringUtils.isNotBlank(customizedBeanName)) {
                    beanName = customizedBeanName;
                }

                result.add(doCreateBeanDefinition(beanName, beanClass.getName()));

                //3、接口注入
                System.out.println(beanClass + " implements interfaces:  " + Arrays.toString(beanClass.getInterfaces()));
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private GPBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setFactoryBeanName(beanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }


    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:", ""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        //jar 、 war 、zip 、rar
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());

        //当成是一个ClassPath文件夹
        for (File file : classPath.listFiles()) {

            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                //全类名 = 包名.类名
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                //Class.forName(className);
                regitryBeanClasses.add(className);
            }
        }
    }

    //自己写，自己用
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        if (Character.isLowerCase(chars[0])) {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

}
