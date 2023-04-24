package com.example.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 基于JDK 反射的动态代理模式

// 不再受限于必须为每个需要被代理的接口实现代理类

// 动态代理可以实现一个较通用的代理工厂，通过 JDK 的 Proxy.newProxyInstance 创建了一个代理对象实例，
// 并通过自定义一个实现 InvocationHandler 接口的类来实现代理的行为。
// Proxy API 背后的原理是基于我们传入 interfaces，
// 动态创建了一个继承 Proxy 类并实现了我们提供的 interfaces 的类的字节码，
// 并通过我们传入的 classLoader 将这个类加载进来
public class JDKProxyFactory {

    private final Object target;

    public JDKProxyFactory(Object target) {
        this.target = target;
    }

    static class ProxyInvocationhandle implements InvocationHandler {
        private final Object target;

        public ProxyInvocationhandle(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("proxy action before");
            Object invoke = method.invoke(proxy, args);
            System.out.println("proxy action after");
            return invoke;
        }
    }


    public Object getProxyInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                new ProxyInvocationhandle(target));
    }

    // 具体如何使用
    //public static void main(){
//       RealSubject realSubject = new realSubject();
//       ProxyFactory proxyFactory = new ProxyFactory(realSubject);
//       Subject subject = proxyFactory.getProxyInstance();
//       subject.action();
//   }
}
