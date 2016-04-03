package com.zhanlibrary.model;


import com.zhanlibrary.utils.GHSStringUtil;

/**
 * 用户信息
 */
public class UserInfo {
    private String member_id;
    private String nick_name;
    private String mobile;
    private String gender;
    private String birthday;
    private String email;
    private String level;
    private String accesstoken;
    private String level_name;
    private String card_id;
    private String uname;
    private String pwd;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUname() {
        return uname;
    }


    public String getLevel_name() {
        return level_name;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getNick_name() {
        if (!GHSStringUtil.isEmpty(mobile) && mobile.equals(nick_name) && mobile.length() > 4) {
            return nick_name.substring(0, 3) + "****" + nick_name.substring(nick_name.length() - 4, nick_name.length());
        }
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        if (gender == null || "".equals(gender)) {
            gender = "2";
        }
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
