package cn.qfei.connect.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 全局返回
 * @author: wxy
 * @date:
 **/
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;

    private boolean success;

    private T data;

    private String msg;

    public R(){
    }

    public R(Integer code, Boolean success, T data, String msg){
        this.code =code;
        this.success = success;
        this.data = data;
        this.msg = msg;
    }

    public static <T> R<T> data(T data){
        return setData(0,true,data,"请求成功");
    }

    public static <T> R<T> data(T data,Integer code){
        return setData(code,true,data,"请求成功");
    }
    public static <T> R<T> fail(String msg) {
        return setData(400,false,null,msg);
    }

    public static <T> R<T> fail(String msg,int code) {
        return setData(code,false,null,msg);
    }
    private static <T> R<T> setData(Integer code, Boolean success, T data, String msg){
        return new R<>(code,success,data,msg==null?"暂无数据":msg);
    }

    public static <T> R<T> success(){
        return new R<>(0,true,null,"请求成功");
    }


}
