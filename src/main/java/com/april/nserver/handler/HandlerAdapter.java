package com.april.nserver.handler;

import com.april.nserver.session.Session;
import com.april.nserver.spliter.Packet;

public class HandlerAdapter implements Handler {

	public void exceptionCaught(Session session) throws Exception {
	}

	public void messageReceived(Session session, Packet packet)
			throws Exception {
	}

	public void messageSent(Session session, Packet packet) throws Exception {
	}

	public void sessionClosed(Session session) throws Exception {
	}

	public void sessionCreated(Session session) throws Exception {
	}

	public void sessionIdle(Session session) throws Exception {
	}

	public void sessionOpened(Session session) throws Exception {
	}
}
