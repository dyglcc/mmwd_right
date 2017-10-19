package abtestdemo.adhoc.example.com.myapplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Created by dongyuangui on 15-5-27.
 */
public class AA {

    public static void main(String[] args){


////        TestFinal testFinal = new TestFinal();
//        try {
//            Field numField = testFinal.getClass().getDeclaredField("NUM");
//            setFinalStatic(numField, 2);
//            System.out.println(numField.get(testFinal));
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            Class clazz = Class.forName("abtestdemo.adhoc.example.com.myapplication.B");
//            try {
//                // can create a constructer;
////                Object obj = clazz.getConstructors()[0].newInstance(this);
//
//                Field field = clazz.getDeclaredField("DEBUG");
//                setFinalStatic(field,true);
//
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        B b = new B();
//        b.sp();
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    class TestFinal {
        private static final int NUM = 1;
    }
}
