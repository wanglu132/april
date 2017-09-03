 /******************************************************************************
    系统名称：中国质量认证中心产品认证业务信息管理平台
 
    文 件 名: com.cqc.cms.test.RecordUpdateLog.java
    说    明: 
    
    版本历史: Since 2015年2月10日 由【wanglu】创建
    备    注: 

    Copyright (c) 2014, cqccms.com.cn  All rights reserved.
 ******************************************************************************/
/**
 * 
 */
package com.april.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注实体类。记录被标识实体类的修改日志（insert,update,delete）
 * @author wanglu
 * @since 2015年2月10日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RecordEntityUpdateLog {

}
