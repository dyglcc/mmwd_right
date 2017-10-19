package abtestdemo.adhoc.example.com.myapplication.reflect;

import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by dongyuangui on 15-5-7.
 */
public class ReflectView {


    public static Object invokeListenerByProxy(Object target){


        ReplaceHandler handler = new ReplaceHandler(target);
        BInterface newObj = (BInterface) Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),handler);
        return newObj;

    }


    public static void invokeListener(final View view){
        try {
            Class clazz = view.getClass().getSuperclass();
            Field field = clazz.getDeclaredField("mListenerInfo");
            try {

                Object obj = ReflectionUtil.getFieldValue(field, view);
                Class clazz_infoListener = obj.getClass();
                Field field1 = ReflectionUtil.getField(clazz_infoListener, "mOnClickListener");
                final View.OnClickListener listener_src = (View.OnClickListener) ReflectionUtil.getFieldValue(field1, obj);

                View.OnClickListener newListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        System.out.println("已经被跟踪");

                        if(listener_src != null){

                            listener_src.onClick(view);
                        }

                    }
                };
                ReflectionUtil.setFieldValue(field1,obj,newListener);


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
