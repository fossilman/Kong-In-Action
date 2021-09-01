package org.fibonacci.framework.httpclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import org.fibonacci.framework.exceptionhandler.ErrorResponse;
import org.fibonacci.framework.exceptions.HttpClientException;
import org.fibonacci.framework.filter.log.LogLevel;
import org.fibonacci.framework.global.ParamVariable;
import org.fibonacci.framework.logcontrol.HttpLogItem;
import org.fibonacci.framework.logcontrol.LogControl;
import org.fibonacci.framework.threadlocal.HttpClientThreadLocal;
import org.fibonacci.framework.threadlocal.ParameterThreadLocal;
import org.fibonacci.framework.util.RequestIdUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//import edu.jiahui.framework.httpclient.mock.MockOpera;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020/11/25
 */
@Slf4j
public class HttpClientTemplate implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final ContentType APPLICATION_FORM_URLENCODED_UTF8 = ContentType
            .create(APPLICATION_X_WWW_FORM_URLENCODED, Consts.UTF_8);

    private static final ContentType MULTIPART_FORM_DATA_UTF8 = ContentType
            .create(MULTIPART_FORM_DATA, Consts.UTF_8);

    private CloseableHttpClient httpClient;

    private PoolingHttpClientConnectionManager connectionManager;

    //private MockOpera mockOpera;

    private int connectTimeout;

    private int socketTimeout;

    private int connectionRequestTimeout;

    private IdleConnectionMonitorThread monitorThread;

    private LogLevel logLevel = LogLevel.ALL;

    private List<String> requestParamOnlyApis = null;

    private List<String> requestParamAndRequestBodyApis = null;

    private List<String> requestParamAndResponseBodyApis = null;

    private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 5000;

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public List<String> getRequestParamOnlyApis() {
        return requestParamOnlyApis;
    }

    public void setRequestParamOnlyApis(List<String> requestParamOnlyApis) {
        this.requestParamOnlyApis = requestParamOnlyApis;
    }

    public List<String> getRequestParamAndRequestBodyApis() {
        return requestParamAndRequestBodyApis;
    }

    public void setRequestParamAndRequestBodyApis(List<String> requestParamAndRequestBodyApis) {
        this.requestParamAndRequestBodyApis = requestParamAndRequestBodyApis;
    }

    public List<String> getRequestParamAndResponseBodyApis() {
        return requestParamAndResponseBodyApis;
    }

    public void setRequestParamAndResponseBodyApis(List<String> requestParamAndResponseBodyApis) {
        this.requestParamAndResponseBodyApis = requestParamAndResponseBodyApis;
    }

    public HttpClientTemplate(int maxTotal, int defaultMaxPerRoute, int connectionTimeout, int socketTimeout) {
        this(maxTotal, defaultMaxPerRoute, connectionTimeout, socketTimeout, LogLevel.ALL);
    }


    public HttpClientTemplate(int maxTotal, int defaultMaxPerRoute, int connectionTimeout, int socketTimeout,
                              LogLevel logLevel) {
        this(maxTotal, defaultMaxPerRoute, connectionTimeout, socketTimeout, DEFAULT_CONNECTION_REQUEST_TIMEOUT,
                LogLevel.ALL);
    }

    public HttpClientTemplate(int maxTotal, int defaultMaxPerRoute, int connectionTimeout, int socketTimeout,
                              int connectionRequestTimeout, LogLevel logLevel) {
        this.connectTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.logLevel = logLevel;
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        monitorThread = new IdleConnectionMonitorThread();
        monitorThread.start();
    }

    public void close() throws IOException {
        httpClient.close();
        connectionManager.close();
        monitorThread.shutdown();
    }

    public String getEntityString(CloseableHttpResponse response, String url) {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            String entityString = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                entityString = EntityUtils.toString(entity, HttpClientThreadLocal.getResponseCharset());
            }

            // TODO 处理redirect
            if (statusCode >= 200 && statusCode < 400) {
                return entityString;
            } else {
                ErrorResponse errorResponse = null;
                try {
                    if (StringUtils.isNotBlank(entityString)) {
                        errorResponse = JSON.parseObject(entityString, ErrorResponse.class);
                    }
                } catch (Exception e) {
                    throw new HttpClientException(entityString, e, url, entityString);
                }
                throw new HttpClientException(statusCode, errorResponse, url, entityString);
            }
        } catch (IOException e) {
            throw new HttpClientException("Parse http response error", e, url);
        }
    }

    /**
     * content-type: application/x-www-form-urlencoded
     *
     * @param uri
     * @return
     */
    public String doGet(String uri) {
        return doGet(uri, null, APPLICATION_FORM_URLENCODED_UTF8);
    }

    /**
     * content-type: application/x-www-form-urlencoded<br>
     * parameters will be concatenated to the uri:<br>
     * uri?param1=value1&param2=value2
     *
     * @param uri
     * @param parameters
     * @return
     */
    public String doGet(String uri, Map<String, String> parameters) {
        return doGet(uri, parameters, APPLICATION_FORM_URLENCODED_UTF8);
    }

    /**
     * content-type: application/x-www-form-urlencoded<br>
     * parameters will be concatenated to the uri:<br>
     * uri?param1=value1&param2=value2
     *
     * @param uri
     * @param parameters
     * @param httpHeaders
     * @return
     */
    public String doGet(String uri, Map<String, String> parameters, Map<String, String> httpHeaders) {
        HttpGet httpGet = new HttpGet(buildUri(uri, parameters, false));
        return execute(APPLICATION_FORM_URLENCODED_UTF8, httpGet, null, httpHeaders);
    }

    /**
     * 复制于上方的 this.doGet，暂时 web 类应用专用，未来有可能拆成单独的一个 jar 包
     *
     * @param uri
     * @param parameters
     * @param httpHeaders
     * @return
     */
    public String wGet(String uri, Map<String, String> parameters, Map<String, String> httpHeaders) {
        HttpGet httpGet = new HttpGet(buildUri(uri, parameters, false));

        for (Map.Entry<String, String> kv : httpHeaders.entrySet()) {
            if ("CONTENT-TYPE".equals(kv.getKey().toUpperCase())) {
                String contentType = kv.getValue();
                if (StringUtils.isNotBlank(contentType)) {
                    return execute(ContentType.create(contentType, Consts.UTF_8), httpGet, null, httpHeaders);
                }
            }
        }

        return execute(APPLICATION_FORM_URLENCODED_UTF8, httpGet, null, httpHeaders);
    }

    /**
     * application/x-www-form-urlencoded
     *
     * @param uri
     * @param httpEntity
     * @param httpHeaders
     * @return
     */
    public String doPost(String uri, HttpEntity httpEntity, String requestParam, Map<String, String> httpHeaders) {


        HttpPost httpPost = new HttpPost(buildUri(uri, null, false));
        httpPost.setEntity(httpEntity);
        return execute(ContentType.APPLICATION_FORM_URLENCODED, httpPost, requestParam, httpHeaders);
    }

    /**
     * @param uri
     * @param requestBody
     * @param contentType
     * @return
     */
    public String doPost(String uri, String requestBody, ContentType contentType) {
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPost(uri, contentType, httpEntity, requestBody, null);
    }

    /**
     * @param uri
     * @param requestBody
     * @param contentType
     * @param httpHeaders
     * @return
     */
    public String doPost(String uri, String requestBody, ContentType contentType, Map<String, String> httpHeaders) {
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPost(uri, contentType, httpEntity, requestBody, httpHeaders);
    }

    /**
     * content-type: application/json<br>
     * object will be converted to a json string
     *
     * @param uri
     * @param object
     * @return
     */
    public String doPost(String uri, Object object) {
        String requestBody = JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPost(uri, ContentType.APPLICATION_JSON, httpEntity, requestBody, null);
    }

    /**
     * @param uri       请求路径
     * @param paramMap  请求参数
     * @param cleanFlag true则不会拼接默认后缀，在调用第三方服务时使用，否则会拼接，在调用内部服务时使用
     * @return java.lang.String
     * @description: 发送post请求, ContentType使用multipart/form-data方式
     * @Author: linjunxu
     * @Date: 2020/3/31 4:01 下午
     */
    public String doPostFormData(String uri, Map<String, Object> paramMap, boolean cleanFlag) {
        String requestBody = this.postFormDataRequestBody(paramMap);
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return this.doPost(uri, MULTIPART_FORM_DATA_UTF8, httpEntity, requestBody, null, cleanFlag);
    }


    /**
     * content-type: application/json<br>
     * object will be converted to a json string
     *
     * @param uri
     * @param object
     * @param httpHeaders
     * @return
     */
    public String doPost(String uri, Object object, Map<String, String> httpHeaders) {
        String requestBody = JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPost(uri, ContentType.APPLICATION_JSON, httpEntity, requestBody, httpHeaders);
    }

    /**
     * content-type: application/json<br>
     * json object will be converted to a json string
     *
     * @param uri
     * @param json
     * @return
     */
    public String doPost(String uri, JSON json) {
        String requestBody = json.toJSONString();
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPost(uri, ContentType.APPLICATION_JSON, httpEntity, requestBody, null);
    }

    /**
     * content-type: application/json<br>
     * json object will be converted to a json string<br>
     * http headers are supported.
     *
     * @param uri
     * @param json
     * @param httpHeaders
     * @return
     */
    public String doPost(String uri, JSON json, Map<String, String> httpHeaders) {
        String requestBody = json.toJSONString();
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPost(uri, ContentType.APPLICATION_JSON, httpEntity, requestBody, httpHeaders);
    }

    /**
     * content-type: application/json<br>
     * object will be converted to a json string
     *
     * @param uri
     * @param object
     * @return
     */
    public String doPatch(String uri, Object object) {
        String requestBody = JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPatch(uri, ContentType.APPLICATION_JSON, httpEntity, requestBody);
    }

    /**
     * content-type: application/json<br>
     * object will be converted to a json string
     *
     * @param uri
     * @param object
     * @return
     */
    public String doPatch(String uri, Object object, SerializerFeature serializerFeature) {
        String requestBody = JSON.toJSONString(object, serializerFeature);
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPatch(uri, ContentType.APPLICATION_JSON, httpEntity, requestBody);
    }

    /**
     * content-type: application/json<br>
     * object will be converted to a json string
     *
     * @param uri
     * @param object
     * @return
     */
    public String doPut(String uri, Object object) {
        String requestBody = JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        StringEntity httpEntity = new StringEntity(requestBody, GlobalConstants.DEFAULT_CHARSET);
        return doPut(uri, ContentType.APPLICATION_JSON, httpEntity, requestBody);
    }

    /**
     * content-type: application/json<br>
     *
     * @param uri
     * @return
     */
    public String doDelete(String uri) {
        return doDelete(uri, APPLICATION_FORM_URLENCODED_UTF8);
    }

    public String doDelete(String uri, Map<String, String> parameters) {
        return this.doDelete(uri, parameters, APPLICATION_FORM_URLENCODED_UTF8);
    }

    private URI buildUri(String uriStr) {
        return buildUri(uriStr, null, false);
    }

    private URI buildUri(String uriStr, Map<String, String> parameters, boolean cleanFlag) {
        try {

            URIBuilder uriBuilder = new URIBuilder(uriStr);

            if (parameters != null && parameters.size() > 0) {
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    uriBuilder.addParameter(parameter.getKey(), parameter.getValue());
                    HttpClientThreadLocal.getRequestParameters().put(parameter.getKey(), parameter.getValue());
                }
            }

            Boolean attachedStatisticsParam = HttpClientThreadLocal.getAttachedStatisticsParam();

            /* 不管统计参数是不是要往下带，下一跳是要计算的！
             * TODO 外部请求（如保利威严格对接口url进行校验，不能携带任何非相关参数,为了通用性调整）
             * */
            if (!cleanFlag && (attachedStatisticsParam == null || attachedStatisticsParam)) {
                //内部携带
                String nextRequestId = RequestIdUtil.buildNextRequestId();
                HttpClientThreadLocal.getRequestParameters().put(ParamVariable.R_I.name(), nextRequestId);

                uriBuilder.addParameter(ParamVariable.R_I.name(), nextRequestId);

                String os = ParameterThreadLocal.getOs();
                if (StringUtils.isNotBlank(os)) {
                    uriBuilder.addParameter(ParamVariable.P_O.name(), os);
                    HttpClientThreadLocal.getRequestParameters().put(ParamVariable.P_O.name(), os);
                }
                String token = ParameterThreadLocal.getToken();
                if (StringUtils.isNotBlank(token)) {
                    uriBuilder.addParameter(ParamVariable.P_T.name(), token);
                    HttpClientThreadLocal.getRequestParameters().put(ParamVariable.P_T.name(), token);
                }

                String uid = ParameterThreadLocal.getUid();
                if (StringUtils.isNotBlank(uid)) {
                    uriBuilder.addParameter(ParamVariable.P_U.name(), uid);
                    HttpClientThreadLocal.getRequestParameters().put(ParamVariable.P_U.name(), uid);
                }

                String name = ParameterThreadLocal.getName();
                if (StringUtils.isNotBlank(name)) {
                    uriBuilder.addParameter(ParamVariable.P_N.name(), name);
                    HttpClientThreadLocal.getRequestParameters().put(ParamVariable.P_N.name(), name);
                }
                String sessionId = ParameterThreadLocal.getSessionId();
                if (StringUtils.isNotBlank(sessionId)) {
                    uriBuilder.addParameter(ParamVariable.P_S.name(), sessionId);
                    HttpClientThreadLocal.getRequestParameters().put(ParamVariable.P_S.name(), sessionId);
                }
                String remoteAddr = ParameterThreadLocal.getRemoteAddr();
                if (StringUtils.isNotBlank(remoteAddr)) {
                    uriBuilder.addParameter(ParamVariable.R_A.name(), remoteAddr);
                    HttpClientThreadLocal.getRequestParameters().put(ParamVariable.R_A.name(), remoteAddr);
                }
                if (StringUtils.isNotBlank(InitParameters.getContextPath())) {
                    uriBuilder.addParameter(ParamVariable.R_E.name(), InitParameters.getContextPath());
                    HttpClientThreadLocal.getRequestParameters().put(ParamVariable.R_E.name(), InitParameters.getContextPath());
                }
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new HttpClientException("Build Url error.", e, uriStr);
        }
    }

    /**
     * base method
     *
     * @param uri
     * @param parameters
     * @param contentType
     * @return
     */
    public String doGet(String uri, Map<String, String> parameters, ContentType contentType) {
        HttpGet httpGet = new HttpGet(buildUri(uri, parameters, false));
        return execute(contentType, httpGet);
    }

    private URI buildUri(String uriStr, Map<String, String> parameters, String charset) {
        try {
            List<NameValuePair> list = Lists.newArrayList();
            if (parameters != null && parameters.size() > 0) {
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(parameter.getKey(), parameter.getValue());
                    list.add(pair);
                    HttpClientThreadLocal.getRequestParameters().put(parameter.getKey(), parameter.getValue());
                }
            }

            // 不管统计参数是不是要往下带，下一跳是要计算的！
            String nextRequestId = RequestIdUtil.buildNextRequestId();
            HttpClientThreadLocal.getRequestParameters().put(ParamVariable.R_I.name(), nextRequestId);

            final StringBuilder sb = new StringBuilder();
            sb.append(uriStr).append("?").append(URLEncodedUtils.format(list, charset));

            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new HttpClientException("Build Url error.", e, uriStr);
        }
    }

    public String doGetWithCharset(String uri, Map<String, String> parameters, String charset) {
        HttpGet httpGet = new HttpGet(buildUri(uri, parameters, charset));
        return execute(ContentType.create(APPLICATION_X_WWW_FORM_URLENCODED, charset), httpGet);
    }

    /**
     * base method
     *
     * @param uri
     * @param parameters
     * @param contentType
     * @param cleanFlag   indicates whether to attache common parameters. True to not.
     * @return
     * @deprecated 请改成用如下方式：1. 调用 <code>omitAttachedStatisticsParamOnce</code> 方法来使当前线程都不添加通用参数。 2. 调用该类的其它 doXXX 方法。
     */
    public String doGet(String uri, Map<String, String> parameters, ContentType contentType, boolean cleanFlag) {
        HttpGet httpGet = new HttpGet(buildUri(uri, parameters, cleanFlag));
        return execute(contentType, httpGet);
    }

    /**
     * base method, without common parameters
     *
     * @param uri
     * @param parameters
     * @return
     * @deprecated 请改成用如下方式：1. 调用 <code>omitAttachedStatisticsParamOnce</code> 方法来使当前线程都不添加通用参数。 2. 调用该类的其它 doXXX 方法。
     */
    public String doGetClean(String uri, Map<String, String> parameters) {
        return doGet(uri, parameters, APPLICATION_FORM_URLENCODED_UTF8, true);
    }

    /**
     * base method, without headers
     *
     * @param uri
     * @param contentType
     * @param httpEntity
     * @return
     */
    public String doPost(String uri, ContentType contentType, HttpEntity httpEntity, String requestBody) {
        return this.doPost(uri, contentType, httpEntity, requestBody, null);
    }

    /**
     * <pre>
     * base method, without common parameters
     * 可以通过<code>omitAttachedStatisticsParamOnce()</code>方法设置不加追踪统计参数
     * </pre>
     *
     * @param uri
     * @param contentType
     * @param httpEntity
     * @return
     */
    @Deprecated
    public String doPostClean(String uri, ContentType contentType, HttpEntity httpEntity, String requestBody) {
        return this.doPost(uri, contentType, httpEntity, requestBody, null, true);
    }

    /**
     * base method, with headers
     *
     * @param uri
     * @param contentType
     * @param httpEntity
     * @return
     */
    public String doPost(String uri, ContentType contentType, HttpEntity httpEntity, String requestBody,
                         Map<String, String> httpHeaders) {
        return this.doPost(uri, contentType, httpEntity, requestBody, httpHeaders, false);
    }

    /**
     * base method, with headers
     *
     * @param uri
     * @param contentType
     * @param httpEntity
     * @return
     * @deprecated 请改成用如下方式：1. 调用 <code>omitAttachedStatisticsParamOnce</code> 方法来使当前线程都不添加通用参数。 2. 调用该类的其它 doXXX 方法。
     */
    public String doPost(String uri, ContentType contentType, HttpEntity httpEntity, String requestBody,
                         Map<String, String> httpHeaders, boolean cleanFlag) {
        HttpPost httpPost = new HttpPost(buildUri(uri, null, cleanFlag));
        httpPost.setEntity(httpEntity);
        return execute(contentType, httpPost, requestBody, httpHeaders);
    }

    /**
     * base method
     *
     * @param uri
     * @param contentType
     * @param httpEntity
     * @return
     */
    public String doPut(String uri, ContentType contentType, HttpEntity httpEntity, String requestBody) {
        HttpPut httpPut = new HttpPut(buildUri(uri));
        httpPut.setEntity(httpEntity);
        return execute(contentType, httpPut, requestBody);
    }

    /**
     * base method
     *
     * @param uri
     * @param contentType
     * @param httpEntity
     * @return
     */
    public String doPatch(String uri, ContentType contentType, HttpEntity httpEntity, String requestBody) {
        HttpPatch httpPatch = new HttpPatch(buildUri(uri));
        httpPatch.setEntity(httpEntity);
        return execute(contentType, httpPatch, requestBody);
    }

    /**
     * base method
     *
     * @param uri
     * @param contentType
     * @param httpEntity
     * @return
     */
    public String doPut(String uri, ContentType contentType, HttpEntity httpEntity, String requestBody, Map<String, String> httpHeaders) {
        HttpPut httpPut = new HttpPut(buildUri(uri));
        httpPut.setEntity(httpEntity);
        return execute(contentType, httpPut, requestBody, httpHeaders);
    }

    public String doDelete(String uri, Map<String, String> parameters, ContentType contentType) {
        HttpDelete httpDelete = new HttpDelete(buildUri(uri, parameters, false));
        return execute(contentType, httpDelete);
    }

    public String doDelete(String uri, Map<String, String> parameters, ContentType contentType, Map<String, String> httpHeaders) {
        HttpDelete httpDelete = new HttpDelete(buildUri(uri, parameters, false));
        return execute(contentType, httpDelete, null, httpHeaders);
    }

    /**
     * base method
     *
     * @param uri
     * @param contentType
     * @return
     */
    public String doDelete(String uri, ContentType contentType) {
        HttpDelete httpDelete = new HttpDelete(buildUri(uri));
        return execute(contentType, httpDelete);
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private String execute(ContentType contentType, HttpRequestBase httpRequest) {
        return this.execute(contentType, httpRequest, null);
    }

    private String execute(ContentType contentType, HttpRequestBase httpRequest, String requestBody) {
        return this.execute(contentType, httpRequest, requestBody, null);
    }

    public void setTimeoutOnce(int connectTimeout, int socketTimeout) {
        HttpClientThreadLocal.setConnectTimeout(connectTimeout);
        HttpClientThreadLocal.setSocketTimeout(socketTimeout);
    }

    /**
     * 设置本次调用的响应编码
     *
     * @param responseCharset
     */
    public void setResponseCharsetOnce(String responseCharset) {
        HttpClientThreadLocal.setResponseCharset(responseCharset);
    }

    public void setCookieSpecOnce(String cookieSpec) {
        HttpClientThreadLocal.setCookieSpec(cookieSpec);
    }

    public void omitAttachedStatisticsParamOnce() {
        HttpClientThreadLocal.setAttachedStatisticsParam(false);
    }

    public void setHttpRequestApi(String requestApi) {
        HttpClientThreadLocal.setRequestApi(requestApi);
    }


    private String execute(ContentType contentType, HttpRequestBase httpRequest, String requestBody,
                           Map<String, String> httpHeaders) {

        //调用mock判断
        String path = httpRequest.getURI().getPath();
        String exeucteResult = this.mockRpc(path);
        if (exeucteResult != null) {
            log.debug("Http/mock数据成功，返回数据:{},url:{}", exeucteResult, path);
            return exeucteResult;
        }

        String url = httpRequest.getURI().toString();
        CloseableHttpResponse httpResponse = null;
        long requestTime = 0;
        long interval = 0;
        String entityString = null;

        String requestApi = null;
        int curConnectTimeout = connectTimeout;
        int curSocketTimeout = socketTimeout;
        String exceptionMessage = null;
        try {
            if (httpHeaders != null && !httpHeaders.isEmpty()) {
                for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
                    httpRequest.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httpRequest.setHeader(CONTENT_TYPE, contentType.toString());
            Integer threadConnectTimeout = HttpClientThreadLocal.getConnectTimeout();
            Integer threadSocketTimeout = HttpClientThreadLocal.getSocketTimeout();
            if (threadConnectTimeout != null) {
                curConnectTimeout = threadConnectTimeout;
            }
            if (threadSocketTimeout != null) {
                curSocketTimeout = threadSocketTimeout;
            }
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(curConnectTimeout)
                    .setSocketTimeout(curSocketTimeout).setConnectionRequestTimeout(connectionRequestTimeout);
            String cookieSpec = HttpClientThreadLocal.getCookieSpec();
            if (cookieSpec != null) {
                requestConfigBuilder.setCookieSpec(cookieSpec);
            }
            httpRequest.setConfig(requestConfigBuilder.build());

            requestTime = System.currentTimeMillis();
            httpResponse = httpClient.execute(httpRequest);
            interval = System.currentTimeMillis() - requestTime;

            Header[] headers = httpResponse.getHeaders("J-Request-Api");
            if (headers != null && headers.length > 0) {
                requestApi = headers[0].getValue();
            } else {
                requestApi = HttpClientThreadLocal.getRequestApi();
            }

            entityString = getEntityString(httpResponse, url);

            return entityString;
        } catch (IOException e) {
            exceptionMessage = e.getMessage();
            throw new HttpClientException("Http client call error.uri:" + httpRequest.getURI(), e, url);
        } finally {
            printLog(httpRequest, httpResponse, requestBody, entityString, requestTime, interval, requestApi,
                    curConnectTimeout, curSocketTimeout, exceptionMessage);

            HttpClientThreadLocal.setConnectTimeout(null);
            HttpClientThreadLocal.setSocketTimeout(null);
            HttpClientThreadLocal.setAttachedStatisticsParam(null);
            HttpClientThreadLocal.setRequestParameters(null);
            HttpClientThreadLocal.setCookieSpec(null);
            HttpClientThreadLocal.setHttpClient(null);
            HttpClientThreadLocal.setRequestApi(null);
            HttpClientThreadLocal.setResponseCharset(null);
            HttpClientThreadLocal.setTraceId(null);
            HttpClientThreadLocal.setSpanId(null);
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("Close httpResponse error.", e);
                }
            }

            httpRequest.releaseConnection();
        }
    }

    public void printLog(HttpRequestBase httpRequest, CloseableHttpResponse response, String requestBody,
                         String entityString, long requestTime, long interval) {
        this.printLog(httpRequest, response, requestBody, entityString, requestTime, interval,
                httpRequest.getURI().toString());
    }

    public void printLog(HttpRequestBase httpRequest, CloseableHttpResponse response, String requestBody,
                         String entityString, long requestTime, long interval, String requestApi) {
        this.printLog(httpRequest, response, requestBody, entityString, requestTime, interval, requestApi,
                connectTimeout, socketTimeout, null);
    }


    public void printLog(HttpRequestBase httpRequest, CloseableHttpResponse response, String requestBody,
                         String entityString, long requestTime, long interval, String requestApi, int curConnectTimeout,
                         int curSocketTimeout, String exceptionMessage) {

        if (logLevel != LogLevel.NONE) {
            Map<HttpLogItem, Object> logMap = getLogMap(httpRequest, response, requestBody, entityString, requestTime,
                    interval, requestApi, exceptionMessage);

            logMap.put(HttpLogItem.logType, LogControl.LogType.HttpOut);
//            logMap.put(HttpLogItem.context, InitParameters.getContextPath());
//            HttpInvokeLogUtil.filterByLength(logMap);
//            putTraceIntoMDC();
//            MDC.put("logType", LogType.HttpOut.name());
            log.info(JSON.toJSONString(logMap));
//            MDC.remove("logType");
        }
    }

    // fix bug that internal inited http request don't have traceId/spanId in MDC
    // TODO 暂时不引入ZipKin
    private void putTraceIntoMDC() {
//        if(!StringUtils.isEmpty(HttpClientThreadLocal.getTraceId()) && StringUtils.isEmpty(MDC.get("traceId")) ){
//            MDC.put("traceId", HttpClientThreadLocal.getTraceId());
//        }
//        if(!StringUtils.isEmpty(HttpClientThreadLocal.getSpanId()) && StringUtils.isEmpty(MDC.get("spanId")) ){
//            MDC.put("spanId", HttpClientThreadLocal.getSpanId());
//        }
    }

    protected Map<HttpLogItem, Object> getLogMap(HttpRequestBase httpRequest, CloseableHttpResponse response,
                                                 String requestBody, String entityString, long requestTime, long interval, String requestApi,
                                                 String exceptionMessage) {
        Map<HttpLogItem, Object> logMap = new LinkedHashMap<HttpLogItem, Object>();
        URI requestUri = httpRequest.getURI();
        logMap.put(HttpLogItem.requestUri, requestUri.toString());
        logMap.put(HttpLogItem.requestApi, requestApi);
        logMap.put(HttpLogItem.requestMethod, httpRequest.getMethod());
        Header contentTypeHeader = httpRequest.getLastHeader(CONTENT_TYPE);
        String requestContentType = null;
        if (contentTypeHeader != null) {
            requestContentType = contentTypeHeader.getValue();
            logMap.put(HttpLogItem.requestContentType, requestContentType);
        }
        logMap.put(HttpLogItem.requestId, ParameterThreadLocal.getRequestId() + "." + ParameterThreadLocal.getCurrentHop());

        Map<String, Boolean> logLevelMap = new HashMap<>();
        if (HttpClientThreadLocal.getPrintableRequestParameters().size() > 0) {
            logMap.put(HttpLogItem.requestParameter, JSONObject.toJSONString(HttpClientThreadLocal.getPrintableRequestParameters()));
        }
        logMap.put(HttpLogItem.requestBody, requestBody);

        logMap.put(HttpLogItem.requestTime, new Date(requestTime));

        if (response != null) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
                logMap.put(HttpLogItem.responseStatus, statusLine.getStatusCode());
            }
            contentTypeHeader = response.getLastHeader(CONTENT_TYPE);
            String responseContentType = null;
            if (contentTypeHeader != null) {
                responseContentType = contentTypeHeader.getValue();
                logMap.put(HttpLogItem.responseContentType, responseContentType);
            }
            logMap.put(HttpLogItem.responseBody, entityString);
            logMap.put(HttpLogItem.responseInterval, interval);
        } else {
            logMap.put(HttpLogItem.connectTimeout, connectTimeout);
            logMap.put(HttpLogItem.socketTimeout, socketTimeout);
            logMap.put(HttpLogItem.exceptionMessage, exceptionMessage);
        }

        return logMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        //mockOpera = applicationContext.getBean(MockOpera.class);
    }

//    @PostConstruct
//    public void enableZipkinTrace(){
//        HttpTracing httpTracing = applicationContext.getBean("httpTracing", HttpTracing.class);
//        httpClient = TracingHttpClientBuilder.create(httpTracing).setConnectionManager(connectionManager).build();
//    }

    /***
     * @description: 封装post请求方式为form时的参数
     * @param map 具体的参数信息
     * @return java.lang.String
     * @Author: linjunxu
     * @Date: 2020/4/1 3:44 下午
     */
    private String postFormDataRequestBody(Map<String, Object> map) {

        if (map == null) {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (String key : map.keySet()) {
            Object o = map.get(key);
            String v = null;
            if (o instanceof String) {
                v = (String) o;
            } else {
                v = JSONObject.toJSONString(o);
            }
            stringBuffer.append(key + "=" + v);
            stringBuffer.append("&");
        }
        return stringBuffer.toString().substring(0, stringBuffer.length() - 1);
    }

    private class IdleConnectionMonitorThread extends Thread {

        private volatile boolean shutdown = false;

        @Override
        public void run() {
            while (!shutdown) {
                try {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connectionManager.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 15 sec
                        connectionManager.closeIdleConnections(15, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("something wrong.", e);
                }
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }

    }

    public String multipartPost(String uri, HttpEntity httpEntity) {
        HttpPost httpPost = null;
        CloseableHttpResponse httpResponse = null;
        String entityString = null;
        long requestTime = 0;
        long interval = 0;
        String requestApi = null;
        String exceptionMessage = null;
        int curConnectTimeout = connectTimeout;
        int curSocketTimeout = socketTimeout;
        try {
            httpPost = new HttpPost(uri);
            httpPost.setEntity(httpEntity);

            Integer threadConnectTimeout = HttpClientThreadLocal.getConnectTimeout();
            Integer threadSocketTimeout = HttpClientThreadLocal.getSocketTimeout();
            if (threadConnectTimeout != null) {
                curConnectTimeout = threadConnectTimeout;
            }
            if (threadSocketTimeout != null) {
                curSocketTimeout = threadSocketTimeout;
            }
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(curConnectTimeout)
                    .setSocketTimeout(curSocketTimeout).setConnectionRequestTimeout(connectionRequestTimeout).build());

            requestTime = System.currentTimeMillis();
            httpResponse = httpClient.execute(httpPost);
            interval = System.currentTimeMillis() - requestTime;

            Header[] headers = httpResponse.getHeaders("J-Request-Api");
            if (headers != null && headers.length > 0) {
                requestApi = headers[0].getValue();
            }

            entityString = getEntityString(httpResponse, uri);
            return entityString;
        } catch (IOException e) {
            exceptionMessage = e.getMessage();
            throw new HttpClientException("Http client call error.uri:" + httpPost.getURI(), e, uri);
        } finally {
            printLog(httpPost, httpResponse, null, entityString, requestTime, interval, requestApi, curConnectTimeout,
                    curSocketTimeout, exceptionMessage);

            HttpClientThreadLocal.setConnectTimeout(null);
            HttpClientThreadLocal.setSocketTimeout(null);

            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("Close httpResponse error.", e);
                }
            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }

    public void forward(String forwardUri) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        forward(request, response, forwardUri, null, null);
    }

    public void forward(HttpServletRequest request, HttpServletResponse response, String forwardUri) {
        forward(request, response, forwardUri, null, null);
    }

    // ** 如果要改动 forward 的实现，记得同步 wForward
    public void forward(HttpServletRequest request, HttpServletResponse response, String forwardUri,
                        List<String> requestHeaderNames, List<String> responseHeaderNames) {

        CloseableHttpResponse httpResponse = null;
        long requestTime = 0;
        long interval = 0;
        String requestApi = null;
        String exceptionMessage = null;
        int curConnectTimeout = connectTimeout;
        int curSocketTimeout = socketTimeout;

        HttpRequestBase httpRequest = null;
        try {

            // set parameters
            @SuppressWarnings("unchecked")
            Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
            URIBuilder uriBuilder = new URIBuilder(forwardUri);
            if (parameterMap != null && !parameterMap.isEmpty()) {
                for (String key : parameterMap.keySet()) {
                    uriBuilder.addParameter(key, parameterMap.get(key)[0]);
                }
            }

            // forward方法也会增加一跳
            String nextRequestId = RequestIdUtil.buildNextRequestId();
            uriBuilder.setParameter(ParamVariable.R_I.name(), nextRequestId);

            URI uri = uriBuilder.build();

            BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
            switch (request.getMethod().toUpperCase()) {
                case "GET":
                    httpRequest = new HttpGet(uri);
                    break;
                case "POST":
                    httpRequest = new HttpPost(uri);
                    // set body
                    basicHttpEntity = new BasicHttpEntity();
                    basicHttpEntity.setContent(request.getInputStream());
                    ((HttpPost) httpRequest).setEntity(basicHttpEntity);
                    break;
                case "PUT":
                    httpRequest = new HttpPut(uri);
                    // set body
                    basicHttpEntity = new BasicHttpEntity();
                    basicHttpEntity.setContent(request.getInputStream());
                    ((HttpPut) httpRequest).setEntity(basicHttpEntity);
                    break;
                case "DELETE":
                    httpRequest = new HttpDelete(uri);
                    break;
                default:
                    throw new HttpClientException("Method [" + request.getMethod() + "] not supported.");
            }

            // set headers
            String contentTypeValue = request.getHeader(CONTENT_TYPE);
            if (StringUtils.isNotBlank(contentTypeValue)) {
                httpRequest.setHeader(CONTENT_TYPE, contentTypeValue);
                log.debug("[send]" + CONTENT_TYPE + ":" + contentTypeValue);
            }
            if (requestHeaderNames != null && requestHeaderNames.size() > 0) {
                for (String headerName : requestHeaderNames) {
                    if (!CONTENT_TYPE.equalsIgnoreCase(headerName)) {
                        String headerValue = request.getHeader(headerName);
                        if (StringUtils.isNotBlank(headerValue)) {
                            httpRequest.setHeader(headerName, headerValue);
                            log.debug("[send]" + headerName + ":" + headerValue);
                        }
                    }
                }
            }

            // set timeout
            Integer threadConnectTimeout = HttpClientThreadLocal.getConnectTimeout();
            Integer threadSocketTimeout = HttpClientThreadLocal.getSocketTimeout();
            if (threadConnectTimeout != null) {
                curConnectTimeout = threadConnectTimeout;
            }
            if (threadSocketTimeout != null) {
                curSocketTimeout = threadSocketTimeout;
            }
            httpRequest.setConfig(RequestConfig.custom().setConnectTimeout(curConnectTimeout)
                    .setSocketTimeout(curSocketTimeout).setConnectionRequestTimeout(connectionRequestTimeout).build());

            requestTime = System.currentTimeMillis();
            httpResponse = httpClient.execute(httpRequest);
            interval = System.currentTimeMillis() - requestTime;

            // 设置应答
            StatusLine sl = httpResponse.getStatusLine();
            if (sl != null) {
                response.setStatus(sl.getStatusCode());
            }
            Header lastHeader = httpResponse.getLastHeader(CONTENT_TYPE);
            if (lastHeader != null) {
                response.setHeader(CONTENT_TYPE, lastHeader.getValue());
            }
            Header[] headers = httpResponse.getHeaders("J-Request-Api");
            if (headers != null && headers.length > 0) {
                requestApi = headers[0].getValue();
            }

            // set headers
            Header contentTypeHeader = httpResponse.getFirstHeader(CONTENT_TYPE);
            if (contentTypeHeader != null) {
                response.setHeader(CONTENT_TYPE, contentTypeHeader.getValue());
                log.debug("[recv]" + CONTENT_TYPE + ":" + contentTypeHeader.getValue());
            }
            if (responseHeaderNames != null && responseHeaderNames.size() > 0) {
                for (String headerName : responseHeaderNames) {
                    if (!CONTENT_TYPE.equalsIgnoreCase(headerName)) {
                        Header header = httpResponse.getFirstHeader(headerName);
                        if (header != null) {
                            response.setHeader(header.getName(), header.getValue());
                            log.debug("[recv]" + header.getName() + ":" + header.getValue());
                        }
                    }
                }
            }

            httpResponse.getEntity().writeTo(response.getOutputStream());

        } catch (IOException | URISyntaxException e) {
            exceptionMessage = e.getMessage();
            throw new HttpClientException("Http client call error.", e, forwardUri);
        } finally {
            printLog(httpRequest, httpResponse, "<Forwarded Request>", "<Forwarded Response>", requestTime, interval,
                    requestApi, curConnectTimeout, curSocketTimeout, exceptionMessage);

            HttpClientThreadLocal.setConnectTimeout(null);
            HttpClientThreadLocal.setSocketTimeout(null);
            HttpClientThreadLocal.setRequestParameters(null);
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("Close httpResponse error.", e);
                }
            }

            if (httpRequest != null) {
                httpRequest.releaseConnection();
            }
        }
    }

    /**
     * 复制于上方的 this.forward，暂时 web 类应用专用，未来有可能拆成单独的一个 jar 包
     *
     * @param request
     * @param response
     * @param forwardUri
     */
    // TODO 暂时注释该方法
    public void wForward(HttpServletRequest request, HttpServletResponse response, String forwardUri) {
//        CloseableHttpResponse httpResponse = null;
//        long requestTime = 0;
//        long interval = 0;
//        String requestApi = null;
//        String exceptionMessage = null;
//        int curConnectTimeout = connectTimeout;
//        int curSocketTimeout = socketTimeout;
//
//        HttpRequestBase httpRequest = null;
//        try {
//            // // set parameters
//            // @SuppressWarnings("unchecked")
//            // Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
//            // URIBuilder uriBuilder = new URIBuilder(forwardUri);
//            // if (parameterMap != null && !parameterMap.isEmpty()) {
//            //     for (String key : parameterMap.keySet()) {
//            //         uriBuilder.addParameter(key, parameterMap.get(key)[0]);
//            //     }
//            // }
//            //
//            // // forward方法也会增加一跳
//            // String nextRequestId = RequestIdUtil.buildNextRequestId();
//            // uriBuilder.setParameter(ParamVariable.R_I.name(), nextRequestId);
//            //
//            // URI uri = uriBuilder.build();
//
//            @SuppressWarnings("unchecked")
//            Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
//            HashMap<String, String> params = new HashMap<>();
//            if (parameterMap != null && !parameterMap.isEmpty()) {
//                for (String key : parameterMap.keySet()) {
//                    params.put(key, parameterMap.get(key)[0]);
//                }
//            }
//
//            // 1. 用户主动带的参数会被传递
//            // 2. ParameterThreadLocal 里修改的值会覆盖用户传的值，而不是重复添加
//            // 3. forwardUri 上手动拼的参数会被重复添加
//            URI uri = this.buildUri(forwardUri, params, false);
//
//            BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
//            switch (request.getMethod().toUpperCase()) {
//                case "GET":
//                    httpRequest = new HttpGet(uri);
//                    break;
//                case "POST":
//                    httpRequest = new HttpPost(uri);
//                    // set body
//                    basicHttpEntity = new BasicHttpEntity();
//                    basicHttpEntity.setContent(request.getInputStream());
//                    ((HttpPost) httpRequest).setEntity(basicHttpEntity);
//                    break;
//                case "PUT":
//                    httpRequest = new HttpPut(uri);
//                    // set body
//                    basicHttpEntity = new BasicHttpEntity();
//                    basicHttpEntity.setContent(request.getInputStream());
//                    ((HttpPut) httpRequest).setEntity(basicHttpEntity);
//                    break;
//                case "DELETE":
//                    httpRequest = new HttpDelete(uri);
//                    break;
//                default:
//                    throw new HttpClientException("Method [" + request.getMethod() + "] not supported.");
//            }
//
//            // ** 默认传输常用/标准的头和以 `X-` 开头的头
//            final List<String> DEFAULT_X_HEADERS = Arrays.asList("REFERER", "CONTENT-DISPOSITION");
//            List<String> xHeaders = new ArrayList<>();
//            xHeaders.addAll(DEFAULT_X_HEADERS);
//
//            Enumeration it = request.getHeaderNames();
//            while (it.hasMoreElements()) {
//                String key = (String) it.nextElement();
//                String keyCaseInsensitive = key.toUpperCase();
//                if (keyCaseInsensitive.startsWith("X-") || DEFAULT_X_HEADERS.contains(keyCaseInsensitive)) {
//                    String value = request.getHeader(key);
//                    if (StringUtils.isNotBlank(value)) {
//                        xHeaders.add(key);
//                    }
//                }
//            }
//
//            // set headers
//            String contentTypeValue = request.getHeader(CONTENT_TYPE);
//            if (StringUtils.isNotBlank(contentTypeValue)) {
//                httpRequest.setHeader(CONTENT_TYPE, contentTypeValue);
//                log.debug("[send]" + CONTENT_TYPE + ":" + contentTypeValue);
//            }
//            if (xHeaders != null && xHeaders.size() > 0) {
//                for (String headerName : xHeaders) {
//                    if (!CONTENT_TYPE.equalsIgnoreCase(headerName)) {
//                        String headerValue = request.getHeader(headerName);
//                        if (StringUtils.isNotBlank(headerValue)) {
//                            httpRequest.setHeader(headerName, headerValue);
//                            log.debug("[send]" + headerName + ":" + headerValue);
//                        }
//                    }
//                }
//            }
//
//            // set timeout
//            Integer threadConnectTimeout = HttpClientThreadLocal.getConnectTimeout();
//            Integer threadSocketTimeout = HttpClientThreadLocal.getSocketTimeout();
//            if (threadConnectTimeout != null) {
//                curConnectTimeout = threadConnectTimeout;
//            }
//            if (threadSocketTimeout != null) {
//                curSocketTimeout = threadSocketTimeout;
//            }
//            httpRequest.setConfig(RequestConfig.custom().setConnectTimeout(curConnectTimeout)
//                    .setSocketTimeout(curSocketTimeout).setConnectionRequestTimeout(connectionRequestTimeout).build());
//
//            requestTime = System.currentTimeMillis();
//            httpResponse = httpClient.execute(httpRequest);
//            interval = System.currentTimeMillis() - requestTime;
//
//            // 设置应答
//            StatusLine sl = httpResponse.getStatusLine();
//            if (sl != null) {
//                response.setStatus(sl.getStatusCode());
//            }
//            Header lastHeader = httpResponse.getLastHeader(CONTENT_TYPE);
//            if (lastHeader != null) {
//                response.setHeader(CONTENT_TYPE, lastHeader.getValue());
//            }
//            Header[] headers = httpResponse.getHeaders("J-Request-Api");
//            if (headers != null && headers.length > 0) {
//                requestApi = headers[0].getValue();
//            }
//
//            // set headers
//            Header contentTypeHeader = httpResponse.getFirstHeader(CONTENT_TYPE);
//            if (contentTypeHeader != null) {
//                response.setHeader(CONTENT_TYPE, contentTypeHeader.getValue());
//                log.debug("[recv]" + CONTENT_TYPE + ":" + contentTypeHeader.getValue());
//            }
//            if (xHeaders != null && xHeaders.size() > 0) {
//                for (String headerName : xHeaders) {
//                    if (!CONTENT_TYPE.equalsIgnoreCase(headerName)) {
//                        Header header = httpResponse.getFirstHeader(headerName);
//                        if (header != null) {
//                            response.setHeader(header.getName(), header.getValue());
//                            log.debug("[recv]" + header.getName() + ":" + header.getValue());
//                        }
//                    }
//                }
//            }
//
//            httpResponse.getEntity().writeTo(response.getOutputStream());
//
//        } catch (IOException e) {
//            exceptionMessage = e.getMessage();
//            throw new HttpClientException("Http client call error.", e, forwardUri);
//        } finally {
//            printLog(httpRequest, httpResponse, "<Forwarded Request>", "<Forwarded Response>", requestTime, interval,
//                    requestApi, curConnectTimeout, curSocketTimeout, exceptionMessage);
//
//            HttpClientThreadLocal.setConnectTimeout(null);
//            HttpClientThreadLocal.setSocketTimeout(null);
//            HttpClientThreadLocal.setRequestParameters(null);
//            if (httpResponse != null) {
//                try {
//                    httpResponse.close();
//                } catch (IOException e) {
//                    log.error("Close httpResponse error.", e);
//                }
//            }
//
//            if(httpRequest != null) {
//                httpRequest.releaseConnection();
//            }
//        }
    }

    /**
     * /cwwcw/pcjebg
     * /cwwcw/
     * /cwwcw
     *
     * @param contextPath
     * @return
     */
    private String mockRpc(String contextPath) {
        try {
            //if (mockOpera != null) {
            //    //判断是否要mock
            //    Boolean hasEnabledMock = mockOpera.hasEnabledMock();
            //    if (hasEnabledMock) {
            //        URI mockUri = URI.create(contextPath);
            //        if (mockUri.getPath() == "" || mockUri.getPath() == "/") {
            //            return null;
            //        }
            //        String applicationName = mockUri.getPath().replaceFirst("/", "");
            //        if (applicationName.indexOf("/") > -1) {
            //            applicationName = applicationName.substring(0, applicationName.indexOf("/"));
            //        }
            //        String path = mockUri.getPath().replaceAll("/", "").replaceAll(applicationName, "");
            //        if (path == null || path == "") {
            //            return null;
            //        }
            //        Mock mock = mockOpera.getMock(applicationName, "/" + path, ParameterThreadLocal.getUid());
            //        return mockOpera.execute(mock);
            //    }
            //}
        } catch (Exception e) {
            log.info("http-mock-rpc is fail", e);
        }
        return null;
    }
}
