/**
 * 管理员类型定义
 */

// 统一响应格式
export interface Result<T = any> {
    code: number;
    msg: string;
    data: T;
}

// 管理员登录 DTO
export interface AdminLoginDTO {
    username: string;
    password: string;
}

// 管理员注册 DTO
export interface AdminRegisterDTO {
    username: string;
    password: string;
    name: string;
}

// 管理员更新 DTO
export interface AdminUpdateDTO {
    name?: string;
    password?: string;
}

// 管理员 VO
export interface AdminVO {
    id: number;
    username: string;
    name: string;
    createTime: string;
}

// 管理员登录响应 VO
export interface AdminLoginVO {
    id: number;
    username: string;
    name: string;
    token: string;
}