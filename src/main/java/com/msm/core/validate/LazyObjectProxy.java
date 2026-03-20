package com.msm.core.validate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LazyObjectProxy {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Object target) {
        if (target instanceof LazyObjectRef lazyRef) {
            return (T) Proxy.newProxyInstance(
                    lazyRef.getClass().getClassLoader(),
                    lazyRef.get().getClass().getInterfaces(), // use interfaces of real object
                    new LazyInvocationHandler(lazyRef)
            );
        }
        return (T) target;
    }


    private static class LazyInvocationHandler implements InvocationHandler {
        private final LazyObjectRef lazyObjectRef;

        public LazyInvocationHandler(LazyObjectRef lazyObjectRef) {
            this.lazyObjectRef = lazyObjectRef;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object realObj = lazyObjectRef.get();
            Object result = method.invoke(realObj, args);
            if (result instanceof LazyObjectRef) {
                return createProxy(result);
            }
            return result;
        }
    }

    private LazyObjectProxy() {}
}
