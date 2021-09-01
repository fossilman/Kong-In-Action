package org.fibonacci.devopscenter.utils;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author krame
 * @description：集合操作类
 * @date ：Created in 2019-07-12 14:58
 */
@Slf4j
public class BeanUtils implements Converter {

    public static void cglibCopyProperties(Class sClass, Class tClass, Object source, Object target,
                                           boolean useConverter) {
        if (source == null) {
            return;
        }
        BeanCopier copier = BeanCopier.create(sClass, tClass, useConverter);
        if (useConverter) {
            copier.copy(source, target, new BeanUtils());
        } else {
            copier.copy(source, target, null);
        }
        copier = null;
    }

    public static <T> List<T> cglibCopyPropertiesList(Class sClass, Class tClass, Object sourceList,
                                                      boolean useConverter) {

        List<T> result = new ArrayList();
        if (isEmptyContainer(sourceList)) {
            return result;
        }
        BeanCopier copier;
        if ((sourceList instanceof List)) {
            List<T> list = (List) sourceList;

            copier = BeanCopier.create(sClass, tClass, useConverter);
            for (T ts : list) {
                try {
                    T tt = (T) tClass.newInstance();
                    if (useConverter) {
                        copier.copy(ts, tt, new BeanUtils());
                    } else {
                        copier.copy(ts, tt, null);
                    }
                    result.add(tt);
                } catch (InstantiationException e) {
                    log.error("cglib对象copy异常", e);
                } catch (IllegalAccessException e) {
                    log.error("cglib对象copy异常", e);
                }
            }
        }
        return result;
    }

    public static final boolean isEmptyContainer(Object container) {
        if (container == null) {
            return true;
        }
        if (container.getClass().isArray()) {
            return Array.getLength(container) == 0;
        }
        if ((container instanceof Collection)) {
            return ((Collection) container).size() == 0;
        }
        if ((container instanceof Map)) {
            return ((Map) container).size() == 0;
        }
        return false;
    }

    public Object convert(Object value, Class target, Object context) {
        if (target.equals(Integer.TYPE)) {
            if (value == null) {
                value = Integer.valueOf(0);
            }
        } else if ((target.equals(String.class)) && (value != null)) {
            value = value + "";
        }
        return value;
    }

    public static final Integer size(Object list) {
        if (list == null) {
            return Integer.valueOf(0);
        }
        if (list.getClass().isArray()) {
            return new Integer(Array.getLength(list));
        }
        if ((list instanceof Collection)) {
            return Integer.valueOf(((Collection) list).size());
        }
        if ((list instanceof Map)) {
            return Integer.valueOf(((Map) list).size());
        }
        return Integer.valueOf(0);
    }

    public static <T> List<T> getSubList(List<T> list, int from, int maxnum) {
        if ((list == null) || (list.size() <= from)) {
            return new ArrayList();
        }
        return new ArrayList(list.subList(from, Math.min(maxnum, list.size())));
    }


    public static final <T> String segmentationChar(List<T> collection, String separator) {
        return isEmptyContainer(collection) ? "" : segmentaValue(collection, separator);
    }

    public static final <T> String segmentationChar(String[] splitList, String separator) {
        return isEmptyContainer(splitList) ? "" : segmentaValue(Arrays.asList(splitList), separator);
    }

    private static <T> String segmentaValue(List<T> dataList, String separator) {
        StringBuffer sbf = new StringBuffer();
        for (T data : dataList) {
            if (data != null) {
                sbf.append(data + separator);
            }
        }
        return sbf.deleteCharAt(sbf.length() - 1).toString();
    }

    public static <T> List getPropertyValueList(Collection<T> beanList, String propertyName, boolean unique) {
        List result = new ArrayList();
        if (beanList == null) {
            return result;
        }
        Iterator<T> beanItor = beanList.iterator();
        while (beanItor.hasNext()) {
            Object bean = beanItor.next();
            try {
                Object pv = getPropertyValue(bean, propertyName);
                if ((pv != null) && ((!unique) || (!result.contains(pv)))) {
                    result.add(pv);
                }
            } catch (Exception e) {
                log.error("获取集合属性值异常！", e);
            }
        }
        return result;
    }

    public static <T> Object getPropertyValue(T bean, String propertyName) {
        if (bean == null) {
            return null;
        }
        if ((bean instanceof Map)) {
            return ((Map) bean).get(propertyName);
        }
        Object obj = null;
        for (Field field : getFildArray(bean)) {
            if (propertyName.equals(field.getName())) {
                try {
                    obj = field.get(bean);
                } catch (IllegalArgumentException e) {
                    log.error("获取集合属性值异常！", e);
                } catch (IllegalAccessException e) {
                    log.error("获取集合属性值异常！", e);
                }
            }
        }
        return obj;
    }


    private static Field[] getFildArray(Object bean) {
        List<Field> fileList = new ArrayList();

        Field[] fields = bean.getClass().getDeclaredFields();

        Field[] superfields = bean.getClass().getSuperclass().getDeclaredFields();
        Collections.addAll(fileList, fields);
        Collections.addAll(fileList, superfields);

        Field[] result = (Field[]) fileList.toArray(new Field[fileList.size()]);
        Field.setAccessible(result, true);
        return result;
    }

    public static <T> void beanIsEmpty(T bean) {
        if (bean == null) {
            return;
        }
        for (Field field : getFildArray(bean)) {
            try {
                if (field.get(bean) == null) {
                    if (field.getType().getCanonicalName().endsWith("Long")) {
                        field.set(bean, Long.valueOf(0L));
                    } else if (field.getType().getCanonicalName().endsWith("Integer")) {
                        field.set(bean, Integer.valueOf(0));
                    } else if (field.getType().getCanonicalName().endsWith("String")) {
                        field.set(bean, "");
                    } else if (field.getType().getCanonicalName().endsWith("Date")) {
                        field.set(bean, new Date());
                    } else if (field.getType().getCanonicalName().endsWith("Boolean")) {
                        field.set(bean, Boolean.valueOf(false));
                    }
                }
            } catch (IllegalArgumentException e) {
                log.error("对象为空属性转换为空异常", e);
            } catch (IllegalAccessException e) {
                log.error("对象为空属性转换为空异常", e);
            }
        }
    }

    public static Map groupBeanList(Collection beanList, String property) {
        return groupBeanList(beanList, property, null);
    }

    private static Map groupBeanList(Collection beanList, String property, Object nullKey) {
        Map result = new LinkedHashMap();
        Iterator it = beanList.iterator();
        while (it.hasNext()) {
            Object bean = it.next();
            try {
                Object keyvalue = getPropertyValue(bean, property);
                if (keyvalue == null) {
                    keyvalue = nullKey;
                }
                if (keyvalue != null) {
                    List tmpList = (List) result.get(keyvalue + "");
                    if (tmpList == null) {
                        tmpList = new ArrayList();
                        result.put(keyvalue + "", tmpList);
                    }
                    tmpList.add(bean);
                }
            } catch (Exception e) {
                log.error("列表分组错误", e);
            }
        }
        return result;
    }

    public static Map getObjectPropertyMap(Collection beanList, String keyname, String valuename) {
        Map result = new HashMap();
        if (beanList == null) {
            return result;
        }
        Iterator it = beanList.iterator();
        while (it.hasNext()) {
            Object bean = it.next();
            try {
                if (bean != null) {
                    result.put(getPropertyValue(bean, keyname) + "", getPropertyValue(bean, valuename) + "");
                }
            } catch (Exception e) {
                log.error("获取集合对应key和value转换错误", e);
            }
        }
        return result;
    }

    public static <T> T trimIsEmpty(Object obj, Class<T> clazz) {
        if (obj == null) {
            if (clazz.getName().contains("String")) {
                obj = "";
            } else if (clazz.getName().contains("Integer")) {
                obj = Integer.valueOf(0);
            } else if (clazz.getName().contains("Long")) {
                obj = Long.valueOf(0L);
            } else if (clazz.getName().contains("Date")) {
                obj = new Date();
            } else if (clazz.getName().contains("Boolean")) {
                obj = Boolean.valueOf(false);
            }
        }
        return (T) obj;
    }

}
