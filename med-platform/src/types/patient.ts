/**
 * 患者类型定义
 */

import type { Result } from './admin';

// 患者 VO
export interface PatientVO {
    id: number;
    username: string;
    name: string;
    gender: number;
    genderDesc?: string;
    age: number;
    phone: string;
    idCard: string;
    createTime: string;
    updateTime: string;
}

// 患者创建 DTO
export interface PatientCreateDTO {
    username: string;
    password: string;
    name: string;
    gender?: number;
    age?: number;
    phone?: string;
    idCard?: string;
}

// 患者更新 DTO
export interface PatientUpdateDTO {
    name?: string;
    password?: string;
    gender?: number;
    age?: number;
    phone?: string;
    idCard?: string;
}

// 患者查询 DTO
export interface PatientQueryDTO {
    username?: string;
    name?: string;
    phone?: string;
    pageNum?: number;
    pageSize?: number;
}

// 分页响应
export interface PageResult<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
}

export interface PatientLoginDTO {
    username: string;
    password: string;
}

// 患者注册 DTO
export interface PatientRegisterDTO {
    username: string;
    password: string;
    name: string;
    phone?: string;
}

// 患者信息更新 DTO
export interface PatientUpdateDTO {
    name?: string;
    phone?: string;
    gender?: number;
    age?: number;
    idCard?: string;
}