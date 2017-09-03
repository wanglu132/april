package com.april.exception;

/**
 * 认证异常
 * @author WL
 *
 */
public class AuthenticateException extends RootException {

	private static final long serialVersionUID = 1691146620478340401L;

	public AuthenticateException(String message) {
		super(message);
	}

}
