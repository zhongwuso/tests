package cust.aowei.jwtstudy.exception;

import cust.aowei.jwtstudy.model.ResultCode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义异常
 * @author aowei
 */
@Getter
public class CommonException extends Exception  {

    private ResultCode resultCode;

    public CommonException(ResultCode resultCode) {
        super(resultCode.message());
        this.resultCode = resultCode;
    }
}
