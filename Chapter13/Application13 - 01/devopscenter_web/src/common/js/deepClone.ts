export const deepClone: any = (data: any) => {
    const type: any = getObjType(data);
    let obj: any;
    if (type === 'array') {
        obj = [];
    } else if (type === 'object') {
        obj = {};
    } else {
        return data;
    }
    if (type === 'array') {
        for (let i: number = 0, len: any = data.length; i < len; i++) {
            obj.push(deepClone(data[i]));
        }
    } else if (type === 'object') {
        for (const key in data) {
            if (obj[key] !== '') {
                obj[key] = deepClone(data[key]);
            }
        }
    }
    return obj;
};

export const getObjType: any = (obj: any): void => {
    const toString: any = Object.prototype.toString;
    const map: any = {
        '[object Boolean]': 'boolean',
        '[object Number]': 'number',
        '[object String]': 'string',
        '[object Function]': 'function',
        '[object Array]': 'array',
        '[object Date]': 'date',
        '[object RegExp]': 'regExp',
        '[object Undefined]': 'undefined',
        '[object Null]': 'null',
        '[object Object]': 'object'
    };
    if (obj instanceof Element) {
        // @ts-ignore
        return 'element';
    }
    return map[toString.call(obj)];
};
