package org.fibonacci.framework.exceptionhandler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.fibonacci.framework.exceptions.BaseException;
import org.fibonacci.framework.exceptions.HttpClientException;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.threadlocal.ParameterThreadLocal;
import org.fibonacci.framework.threadlocal.StatisticsThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            TypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                GlobalErrorResponseConstants.REQUEST_ERROR_CODE + "", GlobalErrorResponseConstants.REQUEST_ERROR_MESSAGE,
                e.getMessage());
        log.warn("[BadRequestWarn]: " + combineRequestMetaAndResponse(errorResponse), e);
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        return convertToResponseEntity(allErrors);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(BindException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        return convertToResponseEntity(allErrors);
    }

    private ResponseEntity<ErrorResponse> convertToResponseEntity(List<ObjectError> allErrors) {
        String developerMessage = "";
        for (ObjectError error : allErrors) {
            developerMessage += error.getDefaultMessage() + "; ";
        }
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                GlobalErrorResponseConstants.REQUEST_ERROR_CODE + "", GlobalErrorResponseConstants.REQUEST_ERROR_MESSAGE,
                developerMessage);
        log.warn("[ClientWarn]: " + combineRequestMetaAndResponse(errorResponse));
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private String combineRequestMetaAndResponse(ErrorResponse errorResponse) {
        return this.combineRequestMetaAndResponse(errorResponse, null);
    }

    private String combineRequestMetaAndResponse(ErrorResponse errorResponse, String url) {
        Map<String, Object> requestMeta = Maps.newHashMap();
        requestMeta.put("requestApi", StatisticsThreadLocal.getApiName());
        requestMeta.put("requestId", ParameterThreadLocal.getRequestId());
        requestMeta.put("httpOutUrl", url);
        requestMeta.putAll(errorResponse.toMap());
        return JSON.toJSONString(requestMeta);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(BaseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getHttpStatus(), e.getCode(), e.getMessage(),
                e.getDeveloperMessage(), e.getErrorLevel());
        if (e instanceof ServerException) {
            log.error("[ServerError]: " + combineRequestMetaAndResponse(errorResponse), e);
        } else {
            log.warn("[ClientWarn]: " + combineRequestMetaAndResponse(errorResponse), e);
        }
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getHttpStatus()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(HttpClientException e) {

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                GlobalErrorResponseConstants.INTERNAL_CALL_ERROR_CODE + "",
                GlobalErrorResponseConstants.INTERNAL_CALL_ERROR_MESSAGE, e.getMessage());

        if (e.getErrorResponse() != null) {
            errorResponse = e.getErrorResponse();
        }

        Integer httpStatusCode = e.getHttpStatusCode();
        if (httpStatusCode != null) {
            httpStatus = HttpStatus.valueOf(httpStatusCode);
            log.warn("[HttpWarn]: " + combineRequestMetaAndResponse(errorResponse, e.getUrl()));
        } else {
            log.error("[HttpError]: " + combineRequestMetaAndResponse(errorResponse, e.getUrl()), e);
        }
        return new ResponseEntity<ErrorResponse>(errorResponse, httpStatus);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                GlobalErrorResponseConstants.COMMON_SERVER_ERROR_CODE + "",
                GlobalErrorResponseConstants.COMMON_SERVER_ERROR_MESSAGE, e.getMessage());
        log.error("[UncaughtError]: " + combineRequestMetaAndResponse(errorResponse), e);
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
