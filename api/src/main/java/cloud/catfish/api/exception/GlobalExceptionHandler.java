package cloud.catfish.api.exception;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理类
 * Created by macro on 2020/2/27.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static ProblemDetail problem(HttpStatusCode status, String detail, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        String uri = request.getRequestURI();
        if (uri != null) {
            pd.setInstance(java.net.URI.create(uri));
        }
        return pd;
    }

    @ExceptionHandler(value = cloud.catfish.api.exception.ApiException.class)
    public ResponseEntity<ProblemDetail> handle(cloud.catfish.api.exception.ApiException e, HttpServletRequest request) {
        log.warn("ApiException: {} {}", request.getMethod(), request.getRequestURI(), e);
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        if (e.getErrorCode() != null) {
            long code = e.getErrorCode().getCode();
            if (code >= 400 && code <= 599) {
                status = HttpStatusCode.valueOf((int) code);
            }
        }
        return ResponseEntity.status(status).body(problem(status, e.getMessage(), request));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("Validation failed: {} {}", request.getMethod(), request.getRequestURI(), e);
        BindingResult bindingResult = e.getBindingResult();
        List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                .map(fe -> {
                    Map<String, String> m = new LinkedHashMap<>();
                    m.put("field", fe.getField());
                    m.put("message", fe.getDefaultMessage());
                    return m;
                })
                .toList();
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Request validation failed", request);
        pd.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ProblemDetail> handleValidException(BindException e, HttpServletRequest request) {
        log.warn("Bind failed: {} {}", request.getMethod(), request.getRequestURI(), e);
        BindingResult bindingResult = e.getBindingResult();
        List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                .map(fe -> {
                    Map<String, String> m = new LinkedHashMap<>();
                    m.put("field", fe.getField());
                    m.put("message", fe.getDefaultMessage());
                    return m;
                })
                .toList();
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Request binding failed", request);
        pd.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public ResponseEntity<ProblemDetail> handleSQLSyntaxErrorException(SQLSyntaxErrorException e, HttpServletRequest request) {
        log.error("SQLSyntaxError: {} {}", request.getMethod(), request.getRequestURI(), e);
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, message, request);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(value = ArithmeticException.class)
    public ResponseEntity<ProblemDetail> handleArithmeticException(ArithmeticException e, HttpServletRequest request) {
        log.error("ArithmeticException: {} {}", request.getMethod(), request.getRequestURI(), e);
        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ProblemDetail> handleBadRequest(Exception e, HttpServletRequest request) {
        log.warn("Bad request: {} {}", request.getMethod(), request.getRequestURI(), e);
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, e.getMessage(), request);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception: {} {}", request.getMethod(), request.getRequestURI(), e);
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, message, request);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        exception.printStackTrace();
        return exception.getMessage();
    }
}
