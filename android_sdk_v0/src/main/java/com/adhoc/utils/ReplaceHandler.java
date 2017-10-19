package com.adhoc.utils;



import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * Created by dongyuangui on 15-5-28.
 */
public class ReplaceHandler implements InvocationHandler{
    private Object instance;
    public ReplaceHandler(Object instance){
        this.instance = instance;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Log.i("invoke", "success");
        System.out.println("invoke sussess");
        Object result = method.invoke(instance,args);
        return result;
    }
}
