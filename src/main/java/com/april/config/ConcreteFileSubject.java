package com.april.config;

import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConcreteFileSubject implements ConfigFileSubject {

	private Log log = LogFactory.getLog(ConcreteFileSubject.class);
	
	private boolean changed = false;

	private Vector<ConfigFileObserver> obs;

	public ConcreteFileSubject() {
		obs = new Vector<ConfigFileObserver>();
	}

	public synchronized void addConfigFileObserver(ConfigFileObserver o) {
		if (o == null)
			throw new NullPointerException();
		if (!obs.contains(o)) {
			obs.addElement(o);
		}
	}

	public synchronized int countConfigFileObserver() {
		return obs.size();
	}

	public synchronized void deleteConfigFileObserver(ConfigFileObserver o) {
		obs.removeElement(o);
	}

	public synchronized void deleteConfigFileObservers() {
		obs.removeAllElements();
	}

	public void notifyConfigFileObserver() {
		notifyConfigFileObserver(null);
	}

	public void notifyConfigFileObserver(Object arg) {
		Object[] arrLocal;
		synchronized (this) {
			if (!changed)
				return;
			arrLocal = obs.toArray();
			clearChanged();
		}
		for (int i = arrLocal.length - 1; i >= 0; i--) {
			if (arg instanceof String) {
				String xml = (String) arg;
				((ConfigFileObserver) arrLocal[i]).update(xml);
				log.info("通知更新类: " + arrLocal[i].getClass().getName());
			} else if (arg instanceof Properties) {
				Properties prop = (Properties) arg;
				((ConfigFileObserver) arrLocal[i]).update(prop);
				log.info("通知更新类: " + arrLocal[i].getClass().getName());
			}
		}
	}

	synchronized void clearChanged() {
		changed = false;
	}

	public synchronized boolean hasChanged() {
		return changed;
	}

	synchronized void setChanged() {
		changed = true;
	}

}
