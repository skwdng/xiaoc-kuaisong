package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

/**
 * 处理SQL完整性约束违反异常的方法
 * 当数据库操作违反完整性约束时（如主键冲突、外键约束等）会被调用
 *
 * @param ex SQL完整性约束违反异常对象，包含具体的异常信息
 * @return 返回一个Result对象，通常用于封装处理结果并返回给前端
 */
@ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
    String message = ex.getMessage();
    if(message.contains("Duplicate entry")){
        String[] split = message.split(" ");
        String usename = split[2];
        String msg = usename + MessageConstant.ALREADY_EXISTS;
        return Result.error(msg);
    }else{
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
    }
    // 方法体内容待实现
}
