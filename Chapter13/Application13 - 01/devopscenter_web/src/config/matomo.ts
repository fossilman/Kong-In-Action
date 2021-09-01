
let matomoUrl: string = '//matomo.jhpy.org/';
let matomoID: string = '2';
// 测试服
if (process.env.VUE_APP_MODE !== 'prod') {
  console.log(222);
  matomoUrl = 'http://172.19.21.247/';
  matomoID = '3';
}

export const MATOMO_URL: string = matomoUrl;
export const MATOMO_ID: string = matomoID;
