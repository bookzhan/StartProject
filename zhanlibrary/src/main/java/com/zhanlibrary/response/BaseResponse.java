package com.zhanlibrary.response;


public class BaseResponse {
    private String rsp;//客户端不用关心这个字段
    private String res;//验证签名使用的,暂时没有验证
    public String flag;//用于保证返回的returndata为空时正常运行

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRsp() {
        return rsp;
    }


    public String getRes() {
        return res;
    }

    public void setRsp(String rsp) {
        this.rsp = rsp;
    }

    public void setRes(String res) {
        this.res = res;
    }

}
