/*
 * Copyright ShangYh
 */
package io.github.thinwind.clusterhouse.web;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.github.thinwind.clusterhouse.aware.ClientDataAware;
import io.github.thinwind.clusterhouse.aware.ServerDataAware;
import io.github.thinwind.clusterhouse.dto.HttpResult;
import io.github.thinwind.clusterhouse.misc.Consts;
import io.github.thinwind.clusterhouse.misc.Errors;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 处理异常时，进行统一处理
 * 
 * @author ShangYehua 2018年4月3日 下午3:17:37
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandleAdvice implements ClientDataAware,ServerDataAware {

    /**
     * undertow info
     */
    private static final String STREAM_IS_CLOSED = "Stream is closed";
    /**
     * undertow info
     */
    private static final String CONNECTION_RESET_BY_PEER = "Connection reset by peer";
    /**
     * tomcat info
     */
    private static final String BROKEN_PIPE = "Broken pipe";


    /**
     * @param e
     * @description JavaBean参数校验异常处理 <br>
     *              JavaBean参数校验错误会抛出 BindException
     **/
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpResult bindExceptionHandler(BindException e, HttpServletRequest req) {
        logErr(req, e, false);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return transferValidErrToResult(fieldErrors, req);
    }

    /**
     * @param e
     * @return com.zhengxl.validationdemo.common.ResultInfo
     *         <p>
     * @description 单个参数校验异常处理 <br>
     *              单个参数校验错误会抛出 ConstraintViolationException
     **/
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpResult constraintViolationExceptionHandler(ConstraintViolationException e,
            HttpServletRequest req) {
        logErr(req, e, false);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String errMsg = constraintViolations.stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
        return createValidationErrorResult(req, errMsg);
    }

    /**
     * @param e
     * @return com.zhengxl.validationdemo.common.ResultInfo
     * @description RequestBody为 json 的参数校验异常捕获
     **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e,
            HttpServletRequest req) {
        logErr(req, e, false);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return transferValidErrToResult(fieldErrors, req);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpResult handler(HttpServletRequest req, IOException e) {
        String msg = logErr(req, e, true);
        // 过滤被关闭的连接
        String causeMsg = ExceptionUtils.getRootCauseMessage(e);
        // tomcat : Broken pipe
        // undertow : Connection reset by peer
        // undertow : Stream is closed
        if (StringUtils.containsIgnoreCase(causeMsg, BROKEN_PIPE)
                || StringUtils.containsIgnoreCase(causeMsg, CONNECTION_RESET_BY_PEER)
                || StringUtils.containsIgnoreCase(causeMsg, STREAM_IS_CLOSED)) {
            return null;
        }
        return makeCommonResult(req, Errors.SERVER_ERROR, msg);
    }

    @ExceptionHandler
    public ResponseEntity<?> handler(HttpServletRequest req, Exception e) {
        String msg = logErr(req, e, true);
        HttpResult result = makeCommonResult(req, Errors.SERVER_ERROR, msg);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    HttpResult transferValidErrToResult(List<FieldError> fieldErrors, HttpServletRequest req) {
        String errorMsg =
                fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining("\n"));
        return createValidationErrorResult(req, errorMsg);
    }

    HttpResult createValidationErrorResult(HttpServletRequest req, String msg) {
        return makeCommonResult(req, Errors.INPUT_PARAM_ERROR, msg);
    }

    private String logErr(HttpServletRequest req, Exception e, boolean logStack) {
        String msg = e.getMessage();
        if (msg == null) {
            msg = e.toString();
        }
        log.error("TraceId:{},Method:{},Path:{},ErrorMsg:{}", req.getAttribute(Consts.TRACE_ID_KEY),
                req.getMethod(), req.getRequestURI(), msg);
        if (logStack) {
            log.error("TraceId:{},StackTrace:{}", req.getAttribute(Consts.TRACE_ID_KEY),
                    ExceptionUtils.getStackTrace(e));
        }
        return msg;
    }


    private HttpResult makeCommonResult(HttpServletRequest req, Errors err, String errMsg) {
        if (errMsg == null) {
            errMsg = err.getDefaultErrMsg();
        }
        return makeCommonResult(req, err.getErrorCode(), errMsg);
    }

    private HttpResult makeCommonResult(HttpServletRequest req, String errCode, String errMsg) {
        HttpResult result = new HttpResult();
        result.setHost(getHost());
        result.setSuccess(false);
        result.setTraceId(getTraceId());
        result.setErrorCode(errCode);
        result.setErrorMessage(errMsg);
        return result;
    }
}
