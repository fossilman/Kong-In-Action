// 代理主机


// api
// export const API: string = '/publish';
// export const API: string = process.env.NODE_ENV === 'development'
//     ? '/devopscenter' : process.env.VUE_APP_API_HOST;

export const API: string = process.env.VUE_APP_API_HOST;

export const DEVOPS_CENTER: string = API + '/devopscenter';

// api mock
export const API_MOCK: string = '/mock_api';

export const CONF_PLUS_MOCK: string = '/confplus';

export const SCHEDULER_PLUS_MOCK: string = '/schedulerplus';

export const MOCKPLUS: string = '/mockplus';

export const ROUTE_PLUS: string = API + '/routeplus';

// api rabbitmqplus
export const API_MQPLUS: string = API + '/rabbitmqplus';

// 项目名
export const PROJECT_NAME: string = 'publish';
// export const PROJECT_NAME: string = 'devopscenter';
