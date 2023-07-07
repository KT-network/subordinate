package com.kt.whose.subordinate.HttpEntity;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;

import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.TypeParser;
import rxhttp.wrapper.utils.Converter;

@Parser(name = "Response")
public class ResponseParser<T> extends TypeParser<T> {

    protected ResponseParser() {
        super();
    }

    public ResponseParser(Type type) {
        super(type);
    }

    @Override
    public T onParse(okhttp3.Response response) throws IOException {


        Response<T> data = Converter.convertTo(response, Response.class, types);

        T t = data.getData();

        if (data.getCode() == 200 && t == null){
            // .toObservableResponse(String.class)
            // 服务器返回提示信息
            //{"code": 200, "msg": "成功", "data": null, "beFrom": "Ks", "time": "2023-07-03 14:18:19"}
            t = (T) data.getMsg();
        }

        if (t == String.class && types[0] == String.class) {
            t = (T) data.getMsg();
        }

        if (data.getCode() == 400 && t == null) {
            // 服务器返回的其他错误信息
            throw new ParseException(String.valueOf(data.getCode()), data.getMsg(), response);
        }

        if (data.getCode() == 405 && t == null) {
            // token过期
            throw new ParseException(String.valueOf(data.getCode()), data.getMsg(), response);
        }

        return t;


    }
}
