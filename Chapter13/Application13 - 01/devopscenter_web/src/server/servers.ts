
import base from './base';
import { DEVOPS_CENTER } from "@/config/env";
import { AxiosPromise, AxiosRequestConfig } from 'axios';
import authors from '@/config/author/index';

//
export function serversList(current: number, size: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/server/list',
        method: 'post',
        data: {
            current,
            size
        }
    };
    return base(config, authors.serverList.router.id);
}

export function serversSurplusList(id: number, name: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + `/server/surplus/list/${id}/${name}`,
        method: 'get'
    };
    return base(config);
}

export function serversUpdate(id: string, ip: string, name: string, remark: string, port: string, team: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/server/update',
        method: 'post',
        data: {
            id,
            ip,
            name,
            port,
            remark,
            team
        }
    };
    return base(config, authors.serversEdit.router.id);
}

export function serversSave(ip: string, name: string, remark: string, port: string, team: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/server/save',
        method: 'post',
        data: {
            ip,
            name,
            remark,
            port,
            team
        }
    };
    return base(config, authors.serversSave.router.id);
}

export function serversDel(id: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/server/del/' + id,
        method: 'get'
    };
    return base(config, authors.serverList.del.id);
}
