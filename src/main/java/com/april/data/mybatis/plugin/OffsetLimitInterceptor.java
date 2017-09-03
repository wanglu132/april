package com.april.data.mybatis.plugin;

import java.util.Properties;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.session.RowBounds;

import com.april.data.mybatis.dialect.Dialect;
import com.april.data.util.PropertiesHelper;

@Intercepts({ @org.apache.ibatis.plugin.Signature(type = org.apache.ibatis.executor.Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class,
		org.apache.ibatis.session.ResultHandler.class }) })
public class OffsetLimitInterceptor implements Interceptor {
	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;
	static int ROWBOUNDS_INDEX = 2;
	static int RESULT_HANDLER_INDEX = 3;
	Dialect dialect;

	public Object intercept(Invocation invocation) throws Throwable {
		processIntercept(invocation.getArgs());
		return invocation.proceed();
	}

	void processIntercept(Object[] queryArgs) {
		MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
		Object parameter = queryArgs[PARAMETER_INDEX];
		RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];
		int offset = rowBounds.getOffset();
		int limit = rowBounds.getLimit();
		if ((this.dialect.supportsLimit())
				&& ((offset != 0) || (limit != 2147483647))) {
			BoundSql boundSql = ms.getBoundSql(parameter);
			String sql = boundSql.getSql().trim();
			if (this.dialect.supportsLimitOffset()) {
				sql = this.dialect.getLimitString(sql, offset, limit);
				offset = 0;
			} else {
				sql = this.dialect.getLimitString(sql, 0, limit);
			}
			limit = 2147483647;

			queryArgs[ROWBOUNDS_INDEX] = new RowBounds(offset, limit);

			BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql);

			MappedStatement newMs = copyFromMappedStatement(ms,
					new BoundSqlSqlSource(newBoundSql));
			queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
		}
	}

	private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql,
			String sql) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql,
				boundSql.getParameterMappings(), boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop,
						boundSql.getAdditionalParameter(prop));
			}
		}
		return newBoundSql;
	}

	private MappedStatement copyFromMappedStatement(MappedStatement ms,
			SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(
				ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null) {
			StringBuilder keyproperties = new StringBuilder();
			int i = 0;
			for (int n = ms.getKeyProperties().length; i < n; i++) {
				keyproperties.append(ms.getKeyProperties()[i]);
				if (i < n - 1) {
					keyproperties.append(",");
				}
			}
			builder.keyProperty(keyproperties.toString());
		}
		builder.timeout(ms.getTimeout());

		builder.parameterMap(ms.getParameterMap());

		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());

		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		String dialectClass = new PropertiesHelper(properties)
				.getRequiredProperty("dialectClass");
		try {
			this.dialect = ((Dialect) Class.forName(dialectClass).newInstance());
		} catch (Exception e) {
			throw new RuntimeException(
					"cannot create dialect instance by dialectClass:"
							+ dialectClass, e);
		}
		System.out.println(OffsetLimitInterceptor.class.getSimpleName()
				+ ".dialect=" + dialectClass);
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return this.boundSql;
		}
	}
}
