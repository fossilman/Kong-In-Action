/**
 * 服务端请求底层封装
 */

import axios, { AxiosRequestConfig, AxiosPromise, AxiosResponse, AxiosError } from "axios";

import {
  ERR_OK
} from '@/config/httpCode';

let uid: string = 'prod';
// let uid: string = 'dev';
if (process.env.VUE_APP_MODE !== 'prod') {
  uid = 'dev';
}

export default function base(propConfig: AxiosRequestConfig, id: number = 0): AxiosPromise {

  const defaultConfig: AxiosRequestConfig = {
    method: 'get', // 默认get 请求,
    headers: {
      //   uid
    },
    timeout: 60000 * 4
  };

  const newConfig: AxiosRequestConfig = Object.assign(defaultConfig, propConfig);

  return new Promise((resolve, reject) => {
    axios(newConfig).then((res: AxiosResponse) => {
      const status: httpCode = res.data.errcode;
      if (status === ERR_OK) {
        resolve(res.data);
      } else {
        reject(res.data);
      }
    }).catch((err: AxiosError) => {
      handleHttpRequestError(err);
    });

  });
}

/**
 * 请求错误处理
 *
 * @param {AxiosError} err
 */
function handleHttpRequestError(err: AxiosError): void {
  console.log(err);
}

