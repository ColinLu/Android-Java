package com.colin.library.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.colin.library.android.helper.UtilHelper;

import java.util.Set;

/**
 * 作者： ColinLu
 * 时间： 2020-10-28 11:41
 * <p>
 * 描述： sp工具类
 */
public final class SpUtil {
    private static final String SP_NAME = "app_sp";
    private static final ArrayMap<String, SparseArray<SharedPreferences>> SP_MAP = new ArrayMap<>();

    private SpUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static boolean put(@NonNull final String key, @NonNull final Object value) {
        return put(SP_NAME, Context.MODE_PRIVATE, key, value, false);
    }

    public static boolean put(@NonNull final String key, @NonNull final Object value, final boolean commit) {
        return put(SP_NAME, Context.MODE_PRIVATE, key, value, commit);
    }

    public static boolean put(@NonNull final String spName, @NonNull final String key, @NonNull final Object value) {
        return put(spName, Context.MODE_PRIVATE, key, value, false);
    }

    public static boolean put(@NonNull final String spName, final int mode, @NonNull final String key, @NonNull final Object value) {
        return put(spName, mode, key, value, false);
    }

    public static void put(@NonNull final String key, @NonNull final Set<String> value) {
        put(SP_NAME, Context.MODE_PRIVATE, key, value, false);
    }

    public static void put(@NonNull final String key, @NonNull final Set<String> value, final boolean commit) {
        put(SP_NAME, Context.MODE_PRIVATE, key, value, commit);
    }

    public static boolean put(@NonNull final String spName, final int mode, @NonNull final String key, @NonNull final Set<String> value, final boolean commit) {
        if (commit) return getSp(spName, mode).edit().putStringSet(key, value).commit();
        else getSp(spName, mode).edit().putStringSet(key, value).apply();
        return false;
    }

    /**
     * 存值
     *
     * @param spName SP 本地保存文件名
     * @param mode   SP 保存模式
     * @param key    关键字
     * @param value  存值 一定要区分Number 类型
     * @param commit 提交方式
     */
    public static boolean put(@NonNull String spName, int mode, @NonNull String key, @NonNull Object value, boolean commit) {
        if (value instanceof String) {
            if (commit) return getSp(spName, mode).edit().putString(key, (String) value).commit();
            else getSp(spName, mode).edit().putString(key, (String) value).apply();
            return false;
        }
        if (value instanceof Boolean) {
            if (commit) return getSp(spName, mode).edit().putBoolean(key, (Boolean) value).commit();
            else getSp(spName, mode).edit().putBoolean(key, (Boolean) value).apply();
            return false;
        }
        if (value instanceof Integer) {
            if (commit) return getSp(spName, mode).edit().putInt(key, (Integer) value).commit();
            else getSp(spName, mode).edit().putInt(key, (Integer) value).apply();
            return false;
        }
        if (value instanceof Float) {
            if (commit) return getSp(spName, mode).edit().putFloat(key, (Float) value).commit();
            else getSp(spName, mode).edit().putFloat(key, (Float) value).apply();
            return false;
        }
        if (value instanceof Long) {
            if (commit) return getSp(spName, mode).edit().putLong(key, (Long) value).commit();
            else getSp(spName, mode).edit().putLong(key, (Long) value).apply();
            return false;
        }
        return false;
    }


    /*String*/
    @Nullable
    public static String getString(@NonNull final String key) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getString(key, null);
    }


    @Nullable
    public static String getString(@NonNull final String key, @Nullable final String def) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getString(key, def);
    }

    public static String getString(@NonNull final String spName, @NonNull final String key, @Nullable final String def) {
        return getSp(spName, Context.MODE_PRIVATE).getString(key, def);
    }

    @Nullable
    public static String getString(@NonNull final String spName, final int mode, @NonNull final String key, @Nullable final String def) {
        return getSp(spName, mode).getString(key, def);
    }

    /*String*/


    /*Boolean*/
    public static boolean getBoolean(@NonNull final String key) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getBoolean(key, Boolean.FALSE);
    }

    public static boolean getBoolean(@NonNull final String key, final boolean def) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getBoolean(key, def);
    }

    public static boolean getBoolean(@NonNull final String spName, @NonNull final String key, final boolean def) {
        return getSp(spName, Context.MODE_PRIVATE).getBoolean(key, def);
    }

    public static boolean getBoolean(@NonNull final String spName, final int mode, @NonNull final String key, final boolean def) {
        return getSp(spName, mode).getBoolean(key, def);
    }
    /*Boolean*/


    /*Integer*/
    public static int getInt(@NonNull final String key) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public static int getInt(@NonNull final String key, final int def) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getInt(key, def);
    }

    public static int getInt(@NonNull final String spName, @NonNull final String key, final int def) {
        return getSp(spName, Context.MODE_PRIVATE).getInt(key, def);
    }

    public static int getInt(@NonNull final String spName, final int mode, @NonNull final String key, final int def) {
        return getSp(spName, mode).getInt(key, def);

    }
    /*Integer*/

    /*Float*/
    public static float getFloat(@NonNull final String key) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getFloat(key, 0F);
    }

    public static float getFloat(@NonNull final String key, final float def) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getFloat(key, def);
    }

    public static float getFloat(@NonNull final String spName, @NonNull final String key, final int def) {
        return getSp(spName, Context.MODE_PRIVATE).getFloat(key, def);
    }

    public static float getFloat(@NonNull final String spName, final int mode, @NonNull final String key, final float def) {
        return getSp(spName, mode).getFloat(key, def);

    }
    /*Float*/


    /*Long*/
    public static long getLong(@NonNull final String key) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getLong(key, 0L);
    }

    public static long getLong(@NonNull final String key, final long def) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getLong(key, def);
    }

    public static float getLong(@NonNull final String spName, @NonNull final String key, final int def) {
        return getSp(spName, Context.MODE_PRIVATE).getLong(key, def);
    }

    public static long getLong(@NonNull final String spName, final int mode, @NonNull final String key, final long def) {
        return getSp(spName, mode).getLong(key, def);

    }
    /*Long*/


    /*set*/
    @Nullable
    public static Set<String> getSet(@NonNull final String key) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getStringSet(key, null);
    }

    @Nullable
    public static Set<String> getSet(@NonNull final String key, @Nullable final Set<String> def) {
        return getSp(SP_NAME, Context.MODE_PRIVATE).getStringSet(key, def);
    }

    @Nullable
    public static Set<String> getSet(@NonNull final String spName, @NonNull final String key, @Nullable final Set<String> def) {
        return getSp(spName, Context.MODE_PRIVATE).getStringSet(key, def);
    }

    @Nullable
    public static Set<String> getSet(@NonNull final String spName, final int mode, @NonNull final String key, @Nullable final Set<String> def) {
        return getSp(spName, mode).getStringSet(key, def);

    }
    /*set*/


    public static boolean contains(@NonNull final String key) {
        return contains(SP_NAME, Context.MODE_PRIVATE, key);
    }

    public static boolean contains(@NonNull final String spName, @NonNull final String key) {
        return contains(spName, Context.MODE_PRIVATE, key);
    }

    /**
     * 查询Sp 是否保存某一个关键词
     *
     * @param spName SP 本地保存文件名
     * @param mode   SP 保存模式
     * @param key    关键字
     * @return 返回 true 含有 关键词  false  不含有 也可能 获取失败
     */
    public static boolean contains(@NonNull final String spName, final int mode, @NonNull final String key) {
        return getSp(spName, mode).contains(key);
    }


    public static void remove(@NonNull final String key) {
        remove(SP_NAME, Context.MODE_PRIVATE, key);
    }

    public static void remove(@NonNull final String spName, @NonNull final String key) {
        remove(spName, Context.MODE_PRIVATE, key);
    }

    /**
     * 移除
     *
     * @param spName SP 本地保存文件名
     * @param mode   SP 保存模式
     * @param key    移除关键字
     */
    public static void remove(@NonNull final String spName, final int mode, @NonNull final String key) {
        getSp(spName, mode).edit().remove(key).apply();
    }

    public static void clear() {
        clear(SP_NAME, Context.MODE_PRIVATE);
    }

    public static void clear(@NonNull final String spName) {
        clear(spName, Context.MODE_PRIVATE);
    }

    /**
     * 清除
     *
     * @param spName SP 本地保存文件名
     * @param mode   SP 保存模式
     */
    public static void clear(@NonNull final String spName, final int mode) {
        getSp(spName, mode).edit().clear().apply();
    }


    @NonNull
    public static SharedPreferences getSp(@NonNull final String spName, final int mode) {
        SparseArray<SharedPreferences> sparseArray = SP_MAP.get(spName);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            final SharedPreferences preferences = UtilHelper.getInstance().getUtilConfig().getApplication().getSharedPreferences(spName, mode);
            sparseArray.put(mode, preferences);
            SP_MAP.put(spName, sparseArray);
            return preferences;
        } else {
            SharedPreferences preferences = sparseArray.get(mode);
            if (null == preferences) {
                preferences = UtilHelper.getInstance().getUtilConfig().getApplication().getSharedPreferences(spName, mode);
                sparseArray.put(mode, preferences);
            }
            return preferences;
        }
    }

}
