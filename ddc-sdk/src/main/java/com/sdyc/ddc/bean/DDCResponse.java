package com.sdyc.ddc.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DDCResponse<T> implements Serializable {
    private boolean success;
    private String message;
    private int code;
    private T data;

    public DDCResponse(){}
    private DDCResponse(boolean success, String message, T data, int code){
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public static <T> DDCResponse<T> success(boolean success){
        return new DDCResponse<>(success,null,null,success?200:500);
    }
    public static<T> DDCResponse<T> success(){
        return new DDCResponse<>(true,null,null,200);
    }
    public static<T> DDCResponse<T> success(T data){
        return new DDCResponse<>(true,null,data,200);
    }
    public static<T> DDCResponse<T> error(String error){
        return new DDCResponse<>(false,error,null,500);
    }
    public static<T> DDCResponse<T> error(String error, int code){
        return new DDCResponse<>(false,error,null,code);
    }

    public static Mapped data(){
        return new Mapped(true,null,new HashMap<>(),200);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class Mapped extends DDCResponse<Map<String,Object>> {
        public Mapped(){}
        private Mapped(boolean success, String message, Map<String,Object> data, int code){
            super(success,message,data,code);
        }
        public Mapped add(String key, Object obj){
            this.getData().put(key,obj);
            return this;
        }
    }

    @Override
    public String toString() {
        return "DDCResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
