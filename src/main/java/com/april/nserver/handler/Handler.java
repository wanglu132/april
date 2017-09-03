package com.april.nserver.handler;

import com.april.nserver.session.Session;
import com.april.nserver.spliter.Packet;

public interface Handler {
	
	void sessionCreated(Session session) throws Exception;

	void sessionOpened(Session session) throws Exception;

	void sessionClosed(Session session) throws Exception;

	void sessionIdle(Session session) throws Exception;

	void exceptionCaught(Session session) throws Exception;

	void messageReceived(Session session, Packet packet) throws Exception;

	void messageSent(Session session, Packet packet) throws Exception;
}
