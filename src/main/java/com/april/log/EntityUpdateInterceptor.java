 /******************************************************************************
    系统名称：中国质量认证中心产品认证业务信息管理平台
 
    文 件 名: com.cqc.cms.test.RecordUpdateInterceptor.java
    说    明: 
    
    版本历史: Since 2015年2月9日 由【wanglu】创建
    备    注: 

    Copyright (c) 2014, cqccms.com.cn  All rights reserved.
 ******************************************************************************/
/**
 * 
 */
package com.april.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author wanglu
 * @since 2015年2月9日
 */
@Intercepts({ @Signature(
				type = Executor.class, 
				method = "update", 
				args = {MappedStatement.class, Object.class}
		    ) })
public class EntityUpdateInterceptor extends ApplicationObjectSupport implements Interceptor {
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		System.out.println("----------before");
		Object ret = invocation.proceed();
		System.out.println("----------after");
		
		MappedStatement ms = (MappedStatement)invocation.getArgs()[0];
		Object entity = invocation.getArgs()[1];
		if(entity.getClass().isAnnotationPresent(RecordEntityUpdateLog.class))
		{
			this.recordUpdateLog(entity, ms);
		}
		
		return ret;
	}
	
	private void recordUpdateLog(Object entity, MappedStatement ms) throws Exception{
		
		EntityUpdateLogDao apdateLogDao = (EntityUpdateLogDao)getApplicationContext().getBean("entityUpdateLogDao");
		
		EntityUpdateLog updateLog = new EntityUpdateLog();
		updateLog.setSqlType(ms.getSqlCommandType().name());
		updateLog.setSqlContent(ms.getBoundSql(entity).getSql());
		updateLog.setEntityName(entity.getClass().getName());
		updateLog.setEntityContent(XmlUtil.obj2xml(entity));
		
		apdateLogDao.insert(updateLog);
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) { }
	
	@Repository
	public interface EntityUpdateLogDao {

		@Insert("insert into log_entity_update (entity_name, entity_content, sql_type, sql_content, oper_account) values (#{entityName}, #{entityContent}, #{sqlType}, #{sqlContent}, #{operAccount})")
		public int insert(EntityUpdateLog updateLog);
	}
	
}

class EntityUpdateLog {

	private Integer id;
	
	private String entityName;
	
	private String entityContent;
	
	private String sqlType;
	
	private String sqlContent;
	
	private String operAccount;
	
	private Date operTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String getSqlContent() {
		return sqlContent;
	}

	public void setSqlContent(String sqlContent) {
		this.sqlContent = sqlContent;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityContent() {
		return entityContent;
	}

	public void setEntityContent(String entityContent) {
		this.entityContent = entityContent;
	}

	public String getOperAccount() {
		return operAccount;
	}

	public void setOperAccount(String operAccount) {
		this.operAccount = operAccount;
	}

	public Date getOperTime() {
		return operTime;
	}

	public void setOperTime(Date operTime) {
		this.operTime = operTime;
	}
}

abstract class XmlUtil {
	
	private final static Class<?>[] ConvertTypes = { String.class };
	
	private final static String XML_LT = "<";
	private final static String XML_GT = ">";
	private final static String XML_LT_SLASH = "</";
	
	public static String obj2xml(Object o) throws Exception
	{
		String objName = o.getClass().getSimpleName();
		
		StringBuilder xml = new StringBuilder();
		
		xml.append(XML_LT).append(objName).append(XML_GT);
		
		Method[] pubMethods = o.getClass().getMethods();
		for(Method pubMethod : pubMethods)
		{
			String pubMethodName = pubMethod.getName();
			if(pubMethodName.startsWith("get") && pubMethod.getParameterTypes().length == 0)
			{
				char[] mna = pubMethodName.substring(3).toCharArray();
				mna[0] = Character.toLowerCase(mna[0]);
				String fieldName = new String(mna);
				
				Field field = null;
				try {
					field = o.getClass().getDeclaredField(fieldName);
				} catch (NoSuchFieldException e) {
					continue;
				}
				
				if(!field.isAnnotationPresent(IgnoreRecordLog.class))
				{
					Class<?> returnType = pubMethod.getReturnType();
					
					boolean convertFlag = false;
					for(Class<?> convertType : ConvertTypes)
					{
						if(returnType.equals(convertType))
						{
							convertFlag = true;
						}
					}
					
					if(returnType.isPrimitive() || convertFlag)
					{
						String returnValue = pubMethod.invoke(o).toString();
						xml.append(XML_LT).append(fieldName).append(XML_GT);
						xml.append(returnValue);
						xml.append(XML_LT_SLASH).append(fieldName).append(XML_GT);
					}
				}
			}
		}
		
		xml.append(XML_LT_SLASH).append(objName).append(XML_GT);
		
		return xml.toString();
	}
	
	public static <T> T xml2obj(String xml)
	{
		throw new UnsupportedOperationException();
	}
}

