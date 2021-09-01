

import base from './base';
import { DEVOPS_CENTER } from "@/config/env";
import { AxiosPromise, AxiosRequestConfig } from 'axios';

//
export function kong(): AxiosPromise {
    const config: AxiosRequestConfig = {
        url: DEVOPS_CENTER + '/check/kong'
    };
    return base(config);
}

