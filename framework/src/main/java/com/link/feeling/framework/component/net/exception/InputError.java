package com.link.feeling.framework.component.net.exception;

/**
 * Created on 2019/1/17  11:08
 * chenpan pan.chen@linkfeeling.cn
 *
 * 错误信息处理
 */
@SuppressWarnings("unused")
public final class InputError {
    String code;
    String msg;
    String name;

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getMsg ()
    {
        return msg;
    }

    public void setMsg (String msg)
    {
        this.msg = msg;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }
}
