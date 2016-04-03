package com.zhanlibrary.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhandalin 2015年08月27日 17:30.
 * 最后修改者: zhandalin  version 1.0
 * 说明:首页的基础信息封装
 */
public class HomeBasesData implements Parcelable {
    public String image;
    private int type;
    private String title;
    private String link;


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeInt(this.type);
        dest.writeString(this.title);
        dest.writeString(this.link);
    }

    public HomeBasesData() {
    }

    protected HomeBasesData(Parcel in) {
        this.image = in.readString();
        this.type = in.readInt();
        this.title = in.readString();
        this.link = in.readString();
    }

    public static final Creator<HomeBasesData> CREATOR = new Creator<HomeBasesData>() {
        @Override
        public HomeBasesData createFromParcel(Parcel source) {
            return new HomeBasesData(source);
        }

        @Override
        public HomeBasesData[] newArray(int size) {
            return new HomeBasesData[size];
        }
    };
}
