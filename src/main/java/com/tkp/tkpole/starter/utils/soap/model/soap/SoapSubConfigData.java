package com.tkp.tkpole.starter.utils.soap.model.soap;

import com.tkp.tkpole.starter.utils.exception.TkpoleException;
import com.tkp.tkpole.starter.utils.exception.TkpoleExceptionPredictable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * 程序员（guojy24）很懒，关于这个类，ta什么也没写╮(╯▽╰)╭
 *
 * <p> 创建时间：2018/8/9
 *
 * @author guojy24
 * @version 1.0
 * */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SoapSubConfigData {

    /**
     * <p> 程序员（guojy24）很懒，关于这个方法，ta什么也没写╮(╯▽╰)╭
     *
     * @return 描述返回值
     * */
    public QName makeOperationName() {
        String[] args = sOperationName_QName.split(",");
        switch ( args.length) {
            case 1: return new QName( args[0]);
            case 2: return new QName( args[0], args[1]);
            case 3: return new QName( args[0], args[1], args[2]);
            default:
                throw TkpoleException.of( TkpoleExceptionPredictable.ERR_CONFIG, String.format("参数格式有误: %s", sOperationName_QName));
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ParamType {
        private String qName;
        private String parameterMode;
    }

    /**
     * 资源名称 (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String name = "";
    /**
     * 资源描述 (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 是)
     * */
    @Builder.Default private String desc = "";

    /**
     * 路径 (默认值: /; 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private String path = "/";
    /**
     * 超时设置 (默认值: 10s; 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    @Builder.Default private Integer sTimeout = 1000*60*30;
    /**
     * operationName (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    private String sOperationName;
    /**
     * operationName(QName) (默认值: 无; 允许值: 无; 是否可重载: 否; 是否必填: 否)
     * */
    private String sOperationName_QName;


    private Map<String, ParamType> sParameter;
    private String sReturnType;
}
