package cloud.catfish.common.exception;

import cloud.catfish.common.api.R;
import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;

/**
 * 全局异常处理类
 * Created by macro on 2020/2/27.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public R handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return R.failed(e.getErrorCode());
        }
        return R.failed(e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return R.validateFailed(message);
    }

    @ExceptionHandler(value = BindException.class)
    public R handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return R.validateFailed(message);
    }

    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public R handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return R.failed(message);
    }

    @ExceptionHandler(value = ArithmeticException.class)
    public ResponseEntity<R> handleArithmeticException(ArithmeticException e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return new ResponseEntity<>(R.failed(message), HttpStatusCode.valueOf(500));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<R> handleException(Exception e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return new ResponseEntity<>(R.failed(message), HttpStatusCode.valueOf(500));
    }

    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        exception.printStackTrace();
        return exception.getMessage();
    }
}
