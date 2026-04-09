package com.medical.smart_medical_server.common;

/**
 * 统一API响应结果封装
 * @param <T> 数据载荷类型
 */
public class Result<T> {

    private Integer code; // 状态码：200成功，其他为失败
    private String msg;   // 提示信息
    private T data;       // 返回数据

    // 私有构造，强制使用静态方法创建
    private Result() {}

    /**
     * 成功 - 无数据返回
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "操作成功";
        return result;
    }

    /**
     * 成功 - 带数据返回
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "操作成功";
        result.data = data;
        return result;
    }

    /**
     * 成功 - 自定义消息
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = msg;
        result.data = data;
        return result;
    }

    /**
     * 失败 - 自定义错误信息
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.msg = msg;
        return result;
    }

    /**
     * 失败 - 自定义状态码和信息
     */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = msg;
        return result;
    }

    // Getter 和 Setter
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}