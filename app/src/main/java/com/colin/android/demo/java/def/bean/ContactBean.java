package com.colin.android.demo.java.def.bean;

import java.util.Objects;

/**
 * 作者： ColinLu
 * 时间： 2022-01-28 22:45
 * <p>
 * 描述： TODO
 */
public class ContactBean {
    public long id;
    public long contact_id;
    public String name;
    public String photo;
    public String number;

    public ContactBean(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactBean)) return false;

        ContactBean bean = (ContactBean) o;

        if (id != bean.id) return false;
        if (contact_id != bean.contact_id) return false;
        if (!Objects.equals(name, bean.name)) return false;
        if (!Objects.equals(photo, bean.photo)) return false;
        return Objects.equals(number, bean.number);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (contact_id ^ (contact_id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "id=" + id +
                ", contact_id=" + contact_id +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
