import base from './base';
import { API_MQPLUS } from "@/config/env";
import { AxiosPromise, AxiosRequestConfig } from 'axios';
import authors from '@/config/author/index';

// 批量重发
export function resendByIds(ids: number[]): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: API_MQPLUS + '/mgr/msg-errors/resend-by-ids',
        method: 'post',
        data: ids
    };
    return base(config, authors.rabbitmqplus.operate.id);
}

// 忽略消息
export function ignoreByIds(ids: number[]): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: API_MQPLUS + '/mgr/msg-errors/ignore-by-ids',
        method: 'post',
        data: ids
    };
    return base(config, authors.rabbitmqplus.operate.id);
}

// 消息列表
export function mgsErrors(context: string, requestId?: string, topic?: string, pageNo: number = 1, pageSize: number = 20): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: API_MQPLUS + '/mgr/msg-errors',
        params: {
            context,
            requestId,
            topic,
            pageNo,
            pageSize
        }
    };
    return base(config);
}

// exchange列表
export function exchanges(context: string, name?: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: API_MQPLUS + '/mgr/exchanges',
        params: {
            context,
            name
        }
    };
    return base(config);
}

// 队列列表
export function queues(exchange: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: API_MQPLUS + '/mgr/queues',
        params: {
            exchange
        }
    };
    return base(config);
}

// 操作记录
export function resendHisTory(requestId: string, pageNo: number = 1, pageSize: number = 20): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: API_MQPLUS + '/mgr/msg-errors/resend-history',
        params: {
            requestId,
            pageNo,
            pageSize
        }
    };
    return base(config);
}
