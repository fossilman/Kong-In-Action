import base from './base';
import { MOCKPLUS } from "@/config/env";
import { AxiosPromise, AxiosRequestConfig } from 'axios';
import authors from '@/config/author/index';

// 新增MockServer
export function addMockServer(realName: string, serverName: string, serverUrl: string, description?: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + '/mockServer/addMockServer',
        method: 'post',
        data: {
            realName,
            serverName,
            serverUrl,
            description
        }
    };
    return base(config, authors.mockserver.add.id);
}


// 删除MockServer
export function deleteMockServer(id: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + `/mockServer/deleteMockServer/${id}`,
        method: 'get'
    };
    return base(config, authors.mockserver.del.id);
}

// 更新MockServer
export function updateMockServer(realName: string, id: number, serverName: string, serverUrl: string, description?: string, mockStatus?: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + '/mockServer/updateMockServer',
        method: 'post',
        data: {
            realName,
            id,
            serverName,
            serverUrl,
            description,
            mockStatus
        }
    };
    return base(config, authors.mockserver.edit.id);
}

// 查询所有mockserver
export function seletAllMockServer(realName: string, pageNo: number = 1, pageSize: number = 20): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + `/mockServer/seletAllMockServer`,
        method: 'post',
        data: {
            realName,
            pageNum: pageNo,
            pageSize
        }
    };
    return base(config);
}

// 查询所有mockserver
export function seletMockServerByName(realName: string, serverName?: string, pageNo: number = 1, pageSize: number = 20): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + `/mockServer/seletMockServerByName`,
        method: 'post',
        data: {
            serverName,
            realName,
            pageNum: pageNo,
            pageSize
        }
    };
    return base(config);
}

// 查询基础配置
export function selectMockConditionConfName(): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + '/mockCondition/selectMockConditionConfName',
        method: 'get'
    };
    return base(config);
}

// 新增MockerServer基础配置
export function addMockCondition(serverId: number, conditionConfName: string, conditionName: string, responseJson: any[], percentage: number[], responseId: number[], inputType: number[]): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + '/mockCondition/addMockCondition',
        method: 'post',
        data: {
            serverId,
            conditionConfName,
            conditionName,
            responseJson,
            percentage,
            responseId,
            inputType
        }
    };
    return base(config, authors.mockserver.add.id);
}

// 删除MockServer基础配置
export function deleteMockCondition(conditionId: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + `/mockCondition/deleteMockCondition/${conditionId}`
    };
    return base(config, authors.mockserver.del.id);
}

// 更新MockServer基础配置
export function updateMockCondition(conditionId: number, serverId: number, conditionConfName: string, conditionName: string, responseJson: any[], percentage: number[], responseId: number[], inputType: number[]): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + '/mockCondition/updateMockCondition',
        method: 'post',
        data: {
            conditionId,
            serverId,
            conditionConfName,
            conditionName,
            responseJson,
            percentage,
            responseId,
            inputType
        }
    };
    return base(config, authors.mockserver.edit.id);
}

// 查看MockServer基础配置信息
export function selectMockCondition(serverId?: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + `/mockCondition/selectMockCondition/${serverId}`,
        method: 'get'
    };
    return base(config);
}


// 删除MockServer
export function deleteMockConfById(id: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + `/mockCondition/deleteMockConfById/${id}`,
        method: 'get'
    };
    return base(config, authors.mockserver.del.id);
}

// 删除MockServer
export function updateMockConfById(id: number, percentage: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + `/mockCondition/updateMockConfById/${id}/${percentage}`,
        method: 'get'
    };
    return base(config, authors.mockserver.del.id);
}


// 更新MockServer基础配置
export function updateMockConditionPriority(conditionIds: number[]): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: MOCKPLUS + '/mockCondition/updateMockConditionPriority',
        method: 'post',
        data: {
            conditionIds
        }
    };
    return base(config, authors.mockserver.edit.id);
}
