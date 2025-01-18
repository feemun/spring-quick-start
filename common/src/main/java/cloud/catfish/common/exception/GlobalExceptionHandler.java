package cloud.catfish.common.exception;

import cloud.catfish.common.api.CommonResult;
import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLSyntaxErrorException;

/**
 * 全局异常处理类
 * Created by macro on 2020/2/27.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public CommonResult handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public CommonResult handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return CommonResult.failed(message);
    }

    @ResponseBody
    @ExceptionHandler(value = ArithmeticException.class)
    public ResponseEntity<CommonResult> handleArithmeticException(ArithmeticException e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return new ResponseEntity<>(CommonResult.failed("5/0"), HttpStatusCode.valueOf(500));
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonResult> handleException(Exception e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return new ResponseEntity<>(CommonResult.failed("5/0"), HttpStatusCode.valueOf(500));
    }

    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        exception.printStackTrace();
        return exception.getMessage();
    }
}
