package me.fanjie.testioc01.ioc;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import me.fanjie.testioc01.L;

/**
 * Created by fanji on 2016/8/2.
 */
public class ViewInjectUtils {

    public static void inject(Activity activity) {
        injectContentView(activity);
        injectViews(activity);
        injectEvent(activity);
    }

    private static void injectContentView(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {
            int contentViewId = contentView.value();
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                method.setAccessible(true);
                method.invoke(activity, contentViewId);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectViews(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Log.e("1",clazz.getName());
        Field[] fields = clazz.getDeclaredFields();
        Log.e("2",String.valueOf(fields));
        for (Field field : fields) {
            Log.e("3",field.getName());
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                int viewId = viewInject.value();
                Log.e("-----injectView", viewId + "");
                if (viewId != -1) {
                    try {
                        Method method = clazz.getMethod("findViewById", int.class);
                        Object resView = method.invoke(activity, viewId);
                        field.setAccessible(true);
                        field.set(activity, resView);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void injectEvent(Activity activity){
        Class<? extends Activity> clazz = activity.getClass();
//获取方法列表
        L.e("获取方法列表");
        Method[] methods = clazz.getMethods();
//        遍历方法
        L.e(methods.toString());
        for (Method method: methods) {
            L.e(method.getName());
//            获取到方法上的注解
            Annotation[] annotations = method.getAnnotations();
//            遍历注解
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
//                获取到元注解
                L.e("获取到元注解");
                EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                if(eventBase!= null){
                    String listenerSetter = eventBase.listenerSetter();
                    String methodName = eventBase.methodName();
                    Class<?> listnerType = eventBase.listenerType();
                    L.e(listenerSetter);
                    L.e(methodName);
                    L.e(listnerType.getName());
                    try {
                        Method aMethod = annotationType.getDeclaredMethod("value");
                        int[] viewIds = (int[]) aMethod.invoke(annotation,null);
//                        获取到第一层注解的value
                        L.e("获取到第一层注解的value    "+ viewIds);

                        DynamicHandler handler = new DynamicHandler(activity);
                        handler.addMethod(methodName,method);
                        Object listener = Proxy.newProxyInstance(listnerType.getClassLoader(),new Class<?>[]{listnerType},handler);
                        for (int viewId :
                                viewIds) {
                            View view = activity.findViewById(viewId);
                            L.e("viewId-----------" + viewId);
                            Method setEventListenerMethod = view.getClass().getMethod(listenerSetter,listnerType);
                            setEventListenerMethod.invoke(view,listener);
                        }


                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
