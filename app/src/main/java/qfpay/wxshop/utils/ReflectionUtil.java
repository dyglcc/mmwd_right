package qfpay.wxshop.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * Created by dongyuangui on 15-5-4.
 */
public class ReflectionUtil {
    /**
     *
     *返回指定字段的Field
     *
     */
    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    /**
     *
     * 设置可以访问
     */
    public static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

    public static Object getFieldValue(Field field,Object obj) throws IllegalAccessException {
        makeAccessible(field);
        return field.get(obj);
    }

   /**
    *  打印声明的所有变量
    * */
    public static void Test(Class clazz, Object obj) throws IllegalAccessException {


        Field[] declaredFields = clazz.getDeclaredFields();
        //获得所有成员变量
        for (int i = 0; i < declaredFields.length; i++) {
            //遍历所有成员变量
            Field field = declaredFields[i];

            //获取成员变量的名字
            System.out.print("名称：" + field.getName() + "\t");

            //获取成员变量类型
            Class fieldType = field.getType();
            System.out.println("类型为：" + fieldType);

            //返回指定对象上词field表示的值
            System.out.println("值为\t" + field.get(obj));
        }
    }

    /**
     * 设置新值
     *
     */
    public static void setFieldValue(Field field1,Object obj, Object newObj) throws IllegalAccessException {

        field1.setAccessible(true);
        field1.set(obj,newObj);
    }
}
