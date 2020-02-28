package cust.aowei.jwtstudy.exception;

import cust.aowei.jwtstudy.model.ResultCode;

import java.text.MessageFormat;

/**
 * 自定义异常
 * @author aowei
 */
public class CustomException extends Exception  {

    private ResultCode resultCode;

    public CustomException(ResultCode resultCode){
        super(resultCode.message());
        this.resultCode = resultCode;
    }

    public CustomException(ResultCode resultCode, Object... args){
        super(resultCode.message());
        String message = MessageFormat.format(resultCode.message(), args);
        resultCode.setMessage(message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode(){
        return resultCode;
    }
}
