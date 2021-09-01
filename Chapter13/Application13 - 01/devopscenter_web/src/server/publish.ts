

import base from './base';
import { DEVOPS_CENTER } from "@/config/env";
import { AxiosPromise, AxiosRequestConfig } from 'axios';
import authors from '@/config/author/index';

// 发布列表
export function publishList(current: number, size: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/list',
        method: 'post',
        data: {
            current,
            size
        }
    };
    return base(config, authors.homeList.router.id);
}

// build详情
export function getBuildDetail(id: number, size: number, current: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/build/detail',
        method: 'post',
        data: {
            buildId: id,
            size,
            current
        }
    };
    return base(config);
}

// deploy详情
export function getDeployDetail(id: number, size: number, current: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/publish/detail',
        method: 'post',
        data: {
            publishId: id,
            size,
            current
        }
    };
    return base(config);
}
// deploy详情明细
export function getDeployDetailMore(id: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/publish/detail/page/' + id,
        method: 'get'
    };
    return base(config);
}


// 添加项目
export function publishSave(name: string, type: string, gatewayType: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/save',
        method: 'post',
        data: {
            applicationName: name,
            type,
            gatewayType
        }
    };
    return base(config, authors.homeSave.router.id);
}

// 添加项目
export function publishEdit(id: number, gatewayType: string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/update',
        method: 'post',
        data: {
            id,
            gatewayType
        }
    };
    return base(config, authors.homeSave.router.id);
}

// 删除项目
export function publishDel(id: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/del/' + id,
        method: 'get'
    };
    return base(config, authors.homeList.operate.id);
}
// 禁用项目
export function publishBan(id: number, status: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + `/projects/stop/${id}/${status}`,
        method: 'get'
    };
    return base(config, authors.homeList.operate.id);
}

// 获取gitlab列表
export function gitLabList(id: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/version/' + id,
        method: 'get'
    };
    return base(config);
}

// 构建 jenkins
export function jenkins(gitlabId: number, gitlabVersion: string, gitlabHead: string, gitlabDesc: string, pushAuthor: string, buildId: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/build',
        method: 'post',
        data: {
            id: buildId,
            gitlabVersion,
            gitlabHead,
            gitlabDesc,
            pushAuthor,
            gitlabId
        }
    };
    return base(config, authors.homeList.build.id);
}

// 监听build
export function buildCheck(id: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/build/check/' + id,
        method: 'get'
    };
    return base(config);
}

// 发布
export function publish(
    buildId: number,
    listId: number,
    gitlabVersion: string,
    gitlabDesc: string,
    serverList: any,
    pushAuthor: string,
    publishType: string,
    beforeGitlabVersion: string,
    vagrancy: number): AxiosPromise {

    let data: any = {};
    if (Number(vagrancy) > 0) {
        data = {
            buildId,
            id: listId,
            serverList,
            pushAuthor,
            publishType,
            gitlabVersion,
            gitlabDesc,
            beforeGitlabVersion,
            vagrancy: Number(vagrancy)
        };
    } else {
        data = {
            buildId,
            id: listId,
            serverList,
            pushAuthor,
            publishType,
            gitlabVersion,
            gitlabDesc
        };
    }

    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/publish',
        method: 'post',
        data
    };
    return base(config, authors.homeList.deploy.id);
}

// 预发布
export function prePublish(
    buildId: number,
    listId: number,
    gitlabVersion: string,
    gitlabDesc: string,
    serverList: any,
    pushAuthor: string,
    publishType: string,
    beforeGitlabVersion: string,
    vagrancy: number): AxiosPromise {

    let data: any = {};
    if (Number(vagrancy) > 0) {
        data = {
            buildId,
            id: listId,
            serverList,
            pushAuthor,
            publishType,
            gitlabVersion,
            gitlabDesc,
            beforeGitlabVersion,
            vagrancy: Number(vagrancy)
        };
    } else {
        data = {
            buildId,
            id: listId,
            serverList,
            pushAuthor,
            publishType,
            gitlabVersion,
            gitlabDesc
        };
    }

    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/log',
        method: 'post',
        data
    };
    return base(config, authors.homeList.deploy.id);
}

// 发布状态监听
export function publishCheck(id: number): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/projects/publish/check/' + id,
        method: 'get'
    };
    return base(config);
}

// 获取发布详情
export function getProjectsId(id: number | string): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + `/projects/detail/${id}`,
        method: 'get'
    };
    return base(config);
}
