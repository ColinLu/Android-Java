package com.colin.android.demo.java;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-03-03 20:53
 * <p>
 * 描述： TODO
 */
public class TestGsonBean {
    @SerializedName("name")    //与json内容相对应
    @Expose()
    private final String mName;

    @SerializedName("list")    //与json内容相对应
    @Expose(deserialize = false) //忽略json 中的字段，json中有都不转化成对象
    private List<String> mList;

    @SerializedName("array")    //与json内容相对应
    @Expose(serialize = false)  //不转化成json字段
    private String[] array;

    public TestGsonBean(@NonNull String name) {
        this.mName = name;
    }

    @Override
    public String toString() {
        return "TestGsonBean{" + "mName='" + mName + '\'' + ", mList=" + mList + ", mArray=" + Arrays.toString(array) + '}';
    }

    public static void main(String[] args) {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                           .create();
        final String[] array = {"A1", "A2"};

        System.out.println("-----------list is null-------------");
        final TestGsonBean list_null = new TestGsonBean("list null");
        list_null.array = array;
        System.out.println(gson.toJson(list_null));

        System.out.println("-----------array is null------------");
        final TestGsonBean array_null = new TestGsonBean("array null");
        array_null.mList = Arrays.asList(array);
        System.out.println(gson.toJson(array_null));

        String gsonString = "{\"name\":\"array null\",\"list\":[\"A1\",\"A2\"]}";
        String gsonString2 = "{\"name\":\"list null\",\"array\":[\"A1\",\"A2\"]}";
        System.out.println("-----------array is null------------");
        final TestGsonBean bean = gson.fromJson(gsonString, TestGsonBean.class);
        System.out.println(bean.toString());

        System.out.println("-----------list is null-------------");
        final TestGsonBean bean2 = gson.fromJson(gsonString2, TestGsonBean.class);
        System.out.println(bean2.toString());

        System.out.println("---------------null-----------------");
        final TestGsonBean bean_null = gson.fromJson("{}", TestGsonBean.class);
        System.out.println(bean_null.toString());

    }
}
