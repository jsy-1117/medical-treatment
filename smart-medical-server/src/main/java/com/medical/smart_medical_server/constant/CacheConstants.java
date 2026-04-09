package com.medical.smart_medical_server.constant;

/**
 * 缓存常量
 */
public class CacheConstants {

    /**
     * 缓存名称
     */
    public static final String CACHE_DEPARTMENT = "department";
    public static final String CACHE_DOCTOR = "doctor";
    public static final String CACHE_DOCTOR_LIST = "doctorList";
    public static final String CACHE_SCHEDULE = "schedule";

    /**
     * 缓存 Key 前缀
     */
    public static final String KEY_DEPARTMENT_ALL = "all";
    public static final String KEY_DEPARTMENT_ID = "id:";
    public static final String KEY_DOCTOR_ID = "id:";
    public static final String KEY_DOCTOR_DEPT = "dept:";
}