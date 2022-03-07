package com.colin.android.demo.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-03-03 21:47
 * <p>
 * 描述： TODO
 */
public final class UserBean {
    public Book english;
    public Book chinese;

    public UserBean(Book english, Book chinese) {
        this.english = english;
        this.chinese = chinese;
    }

    public static class Book {
        public String name;
        public List<String> list;
        public String[] array;

        public Book(String name, List<String> list, String[] array) {
            this.name = name;
            this.list = list;
            this.array = array;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "name='" + name + '\'' +
                    ", list=" + list +
                    ", array=" + Arrays.toString(array) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "english=" + english +
                ", chinese=" + chinese +
                '}';
    }

    public static void main(String[] args) {
        String[] array_en = {"Colin", "Lu", "ColinLu"};
        String[] array_c = {"中国", "四川", "成都"};
        String gsonString = "{\"english\":{\"name\":\"English\",\"list\":[\"Colin\",\"Lu\",\"ColinLu\"]},\"chinese\":{\"name\":\"中文\",\"array\":[\"中国\",\"四川\",\"成都\"]}}";
        final Book chinese = new Book("中文", null, array_c);
        final Book english = new Book("English", Arrays.asList(array_en), null);
        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        final UserBean userBean = new UserBean(english, chinese);
        System.out.println(userBean.toString());
        System.out.println("--------------------------");
        System.out.println(gson.toJson(userBean));
        System.out.println("--------------------------");
    }


}
