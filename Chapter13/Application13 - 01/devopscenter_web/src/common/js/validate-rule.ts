import { ElForm } from 'element-ui/types/form';

export const routePoolFrom: ElForm['rules'] = {
    outPath: [
        { required: true, message: '请输入外部服务路径', trigger: 'blur' },
        { pattern: /^\/[^\s]*/, trigger: 'blur', message: '请输入正确url地址 例如/home' }
    ],
    innerPath: [
        { required: true, message: '请输入内部服务路径', trigger: 'blur' },
        { pattern: /^\/[^\s]*/, trigger: 'blur', message: '请输入正确url地址 例如/home' }
    ]
};

// conRule
export const conRule: ElForm['rules'] = {
    conditionConfName: [{ validator: isNotEmpty, trigger: 'blur' }]
};

// mockRule
export const mockRule: ElForm['rules'] = {
    serverName: [{ validator: isNotEmpty, trigger: 'blur' }],
    serverUrl: [
        { validator: isNotEmpty, trigger: 'blur', message: '请输入正确url地址' },
        { validator: noCNString, trigger: 'blur' },
        { pattern: /^\/[^\s]*/, trigger: 'blur', message: '请输入正确url地址 例如/home' }
    ]
};

// 调度中心Job表单校验规则
export const jobRule: ElForm['rules'] = {
    jobName: [{ validator: isNotEmpty, trigger: 'blur' }, { validator: noCNString, trigger: 'blur' }],
    concurrency: [{ validator: isPositiveInteger, trigger: 'blur' }],
    globalConcurrency: [{ validator: isPositiveInteger, trigger: 'blur' }],
    retryTimes: [{ validator: isPositiveInteger, trigger: 'blur' }],
    retryInterval: [{ validator: isPositiveInteger, trigger: 'blur' }],
    timeout: [{ validator: isPositiveInteger, trigger: 'blur' }]
};

// 调度中心Trigger表单校验规则
export const triggerRule: ElForm['rules'] = {
    triggerName: [{ validator: isNotEmpty, trigger: 'blur' }, { validator: noCNString, trigger: 'blur' }]
    // intervalInSeconds: [{ validator: isPositiveInteger, trigger: 'blur' }],
    // repeatCount: [{ validator: isInteger, trigger: 'blur' }]
};

/**
 * 校验是否为整数
 *
 * @export
 * @param {*} rule
 * @param {number} value
 * @param {(error?: Error) => void} callback
 */
export function isInteger(rule: any, value: string | number, callback: (error?: Error) => void): void {
    const value_num: number = typeof value === 'string' ? parseInt(value) : value;
    if (Number.isInteger(value_num)) {
        callback();
    } else {
        callback(new Error('请输入一个整数'));
    }
}

/**
 * 校验是否为正整数
 *
 * @export
 * @param {*} rule
 * @param {(string | number)} value
 * @param {(error?: Error) => void} callback
 */
export function isPositiveInteger(rule: any, value: string | number, callback: (error?: Error) => void): void {
    const value_num: number = typeof value === 'string' ? parseInt(value) : value;
    if (Number.isInteger(value_num) && value_num >= 0) {
        callback();
    } else {
        callback(new Error('请输入一个正整数'));
    }
}

/**
 * 校验是否大于0
 *
 * @export
 * @param {*} rule
 * @param {(string | number)} value
 * @param {(error?: Error) => void} callback
 */
export function isGreaterThan0(rule: any, value: string | number, callback: (error?: Error) => void): void {
    const value_num: number = typeof value === 'string' ? parseInt(value) : value;
    if (Number.isFinite(value_num) && value_num > 0) {
        callback();
    } else {
        callback(new Error('该数值必须大于0'));
    }
}


/**
 * 校验是否没有包含中文字符
 *
 * @export
 * @param {*} rule
 * @param {string} value
 * @param {(error?: Error) => void} callback
 */
export function noCNString(rule: any, value: string, callback: (error?: Error) => void): void {
    const cn_reg: RegExp = new RegExp(/[\u4e00-\u9fa5]/g);
    if (cn_reg.test(value)) {
        callback(new Error('不能使用中文字符'));
    } else {
        callback();
    }
}

/**
 * 校验是否字段是否为空
 * 同时如果为string类型会判断是否为空字符串
 * 同时如果为number类型会判断是否为有穷数
 *
 * @export
 * @param {*} rule
 * @param {(string | number)} value
 * @param {(error?: Error) => void} callback
 * @returns {void}
 */
export function isNotEmpty(rule: any, value: string | number, callback: (error?: Error) => void): void {
    if (typeof value === 'string' && value !== '') {
        return callback();
    } else if (typeof value === 'number' && Number.isFinite(value)) {
        return callback();
    } else {
        return callback(new Error('该字段不能为空'));
    }
}
