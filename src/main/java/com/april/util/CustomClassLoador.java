package com.april.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomClassLoador extends ClassLoader {

	public CustomClassLoador() {
	}
	
	public Class<?> load_class(String className) {
		String classFile = className.replace(".", "/") + ".class";
		String path = this.getResource(classFile).getPath();
		byte[] cbs = null;
		try {
			InputStream in = new FileInputStream(path);
			BufferedInputStream bf = new BufferedInputStream(in);

			cbs = new byte[bf.available()];
			bf.read(cbs);
			in.close();
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.defineClass(className, cbs, 0, cbs.length);
	}
	
	public static void main(String[] args) {
		String path = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
//		File file = new File(path);
//		String name = file.getName();
//		
//		System.out.println(name.substring(name.indexOf(".") + 1)); 
//		ConfigFile.getInstance().addFile(path);
//		ConfigFile cf = ConfigFile.getInstance();
//		cf.addFile(path);
//		Updater up = new Updater(cf);
//		up.start(2, 10);
		CustomClassLoador ccl = new CustomClassLoador();
		
		List<Package> ls = new ArrayList<Package>();
		
		Package[] packs = ccl.getPackages();
		for(Package pack : packs){
			String packName = pack.getName();
//			System.out.println(packName);
			if(packName.startsWith("com.april.") || packName.startsWith("org.apache.")){
				ls.add(pack);
			}
		}
		
		for(Package pack : ls){
			String packName = pack.getName();
			System.out.println(packName);
		}
	}
}
