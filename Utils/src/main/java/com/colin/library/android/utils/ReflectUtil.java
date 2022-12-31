package com.colin.library.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 作者： ColinLu
 * 时间： 2020-10-27 19:50
 * <p>
 * 描述： 常用反射
 * Modify from https://github.com/didi/booster/blob/master/booster-android-instrument/src/main/java/com/didiglobal/booster/instrument/Reflection.java
 */
public final class ReflectUtil {
    private ReflectUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*反射获取当前 Activity*/
    @Nullable
    public static Activity getActivity(@Nullable final Context context) {
        if (context != null && context.getClass().getName().equals("com.android.internal.policy.DecorContext")) {
            try {
                Field mActivityContextField = context.getClass().getDeclaredField("mActivityContext");
                mActivityContextField.setAccessible(true);
                //noinspection unchecked
                return ((WeakReference<Activity>) mActivityContextField.get(context)).get();
            } catch (Exception e) {
                LogUtil.log(e);
            }
        }
        return null;
    }

    /*获取Application*/
    @Nullable
    @SuppressLint("PrivateApi")
    private static Application getApplicationByReflect() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app instanceof Application) return (Application) app;
        } catch (Exception e) {
            LogUtil.log(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getStaticFieldValue(@Nullable final Class<?> cls, @Nullable final String name) {
        try {
            final Field field = getField(cls, name);
            if (null != field) {
                field.setAccessible(true);
                return (T) field.get(cls);
            }
        } catch (final Throwable t) {
            LogUtil.log(t);
        }
        return null;
    }

    public static boolean setStaticFieldValue(@Nullable final Class<?> cls, @Nullable final String name, @Nullable final Object value) {
        try {
            final Field field = getField(cls, name);
            if (field != null) {
                field.setAccessible(true);
                field.set(cls, value);
                return true;
            }
        } catch (final Throwable t) {
            LogUtil.log(t);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getFieldValue(@Nullable final Object obj, @Nullable final String name) {
        if (obj == null) return null;
        try {
            final Field field = getField(obj.getClass(), name);
            if (field != null) {
                field.setAccessible(true);
                return (T) field.get(obj);
            }
        } catch (final Throwable t) {
            LogUtil.log(t);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(@Nullable final Object obj, @NonNull final Class<?> type) {
        if (obj == null) return null;
        try {
            final Field field = getField(obj.getClass(), type);
            if (field != null) {
                field.setAccessible(true);
                return (T) field.get(obj);
            }
        } catch (final Throwable t) {
            LogUtil.log(t);
        }
        return null;
    }

    public static boolean setFieldValue(@Nullable final Object obj, @Nullable final String name, @Nullable final Object value) {
        if (obj == null) return false;
        try {
            final Field field = getField(obj.getClass(), name);
            if (null != field) {
                field.setAccessible(true);
                field.set(obj, value);
                return true;
            }
        } catch (final Throwable t) {
            LogUtil.log(t);
        }
        return false;
    }

    @Nullable
    public static <T> T newInstance(@NonNull final String className, @NonNull final Object... args) {
        try {
            return newInstance(Class.forName(className), args);
        } catch (final ClassNotFoundException e) {
            LogUtil.log(e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T newInstance(@NonNull final Class<?> clazz, @NonNull Object... args) {
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        loop:
        for (final Constructor<?> constructor : constructors) {
            final Class<?>[] types = constructor.getParameterTypes();
            if (types.length == args.length) {
                for (int i = 0; i < types.length; i++) {
                    if (null != args[i] && !types[i].isAssignableFrom(args[i].getClass())) {
                        continue loop;
                    }
                }
                try {
                    constructor.setAccessible(true);
                    return (T) constructor.newInstance(args);
                } catch (final Throwable t) {
                    LogUtil.log(t);
                    return null;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStaticMethod(final Class<?> klass, final String name) {
        return invokeStaticMethod(klass, name, new Class[0], new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStaticMethod(@Nullable final Class<?> klass, @Nullable final String name, @Nullable final Class[] types, @Nullable final Object[] args) {
        if (null != klass && null != name && null != types && null != args && types.length == args.length) {
            try {
                final Method method = getMethod(klass, name, types);
                if (null != method) {
                    method.setAccessible(true);
                    return (T) method.invoke(klass, args);
                }
            } catch (final Throwable t) {
                LogUtil.log(t);
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(@Nullable final Object obj, @Nullable final String name) {
        return invokeMethod(obj, name, new Class<?>[0], new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(@Nullable final Object obj, @Nullable final String name, @Nullable final Class<?>[] types, @Nullable final Object[] args) {
        if (null != obj && null != name && null != types && null != args && types.length == args.length) {
            try {
                final Method method = getMethod(obj.getClass(), name, types);
                if (null != method) {
                    method.setAccessible(true);
                    return (T) method.invoke(obj, args);
                }
            } catch (final Throwable t) {
                LogUtil.log(t);
            }
        }

        return null;
    }

    @Nullable
    public static Field getField(@Nullable final Class<?> cls, @Nullable final String name) {
        if (cls == null || StringUtil.isEmpty(name)) return null;
        try {
            return cls.getDeclaredField(name);
        } catch (final NoSuchFieldException e) {
            final Class<?> parent = cls.getSuperclass();
            return getField(parent, name);
        }
    }

    @Nullable
    public static Field getField(@NonNull final Class<?> cls, @NonNull final Class<?> type) {
        final Field[] fields = cls.getDeclaredFields();
        if (fields.length <= 0) {
            final Class<?> parent = cls.getSuperclass();
            return parent == null ? null : getField(parent, type);
        }
        for (final Field field : fields) {
            if (field.getType() == type) return field;
        }
        return null;
    }

    @Nullable
    private static Method getMethod(@NonNull final Class<?> cls, @NonNull final String name, @NonNull final Class<?>[] types) {
        try {
            return cls.getDeclaredMethod(name, types);
        } catch (final NoSuchMethodException e) {
            final Class<?> parent = cls.getSuperclass();
            return parent == null ? null : getMethod(parent, name, types);
        }
    }
}
