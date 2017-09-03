package com.april.lifecycle;

public class StartUp {

	private Hook hook;

	public StartUp(Hook hook) {
		this.hook = hook;
	}
	
	public void start(){
		hook.init();
//		hook.config();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				hook.exit();
			}
		});
	}
}
