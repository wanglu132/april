package com.april.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	private List<String> absoluteFileNames = new ArrayList<String>();

	private List<String> relativeFileNames = new ArrayList<String>();

	public String readHtmlFormatFile_zh(String path) throws IOException {
		String cpath = Thread.currentThread().getContextClassLoader()
				.getResource(path).getPath();
		Reader reader = new FileReader(new File(cpath));
		BufferedReader input = new BufferedReader(reader);
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = input.readLine()) != null) {
			int n = line.length();
			char c = ' ';
			for (int i = 0; i < n; i++) {
				c = line.charAt(i);
				if (c == ' ') {
					buffer.append("&nbsp;");
				} else
					buffer.append(c);
			}
			buffer.append("&#13;");
		}
		String res = URLDecoder.decode(buffer.toString(), "UTF-8");
		return res;
	}

	public String readFileToString(String path) {
		String fileString = null;
		try {
			FileChannel fc = new FileInputStream(path).getChannel();
			ByteBuffer bf = ByteBuffer.allocate(1024);
			fc.read(bf);
			bf.flip();
			byte[] fb = new byte[bf.remaining()];
			bf.get(fb);
			fileString = new String(fb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileString;
	}

	public List<String> getAbsoluteFilesInDirectory(String directoryPath,
			String fileSuffix) {
		getAbsoluteFilesInDirectoryReal(directoryPath, fileSuffix);
		return absoluteFileNames;
	}

	public List<String> getRelativeFilesInDirectory(String directoryPath,
			String fileSuffix) {
		String dp = directoryPath;
		List<String> fls = getAbsoluteFilesInDirectory(directoryPath,
				fileSuffix);
		dp = dp.replace('/', '\\').substring(1);
		for (String f : fls) {
			relativeFileNames.add(f.replace(dp, ""));
		}
		return relativeFileNames;
	}

	private void getAbsoluteFilesInDirectoryReal(String directoryPath,
			String fileSuffix) {

		File parentF = new File(directoryPath);
		if (parentF.isFile()) {
			if (directoryPath.endsWith(fileSuffix)) {
				absoluteFileNames.add(directoryPath);
			}
			return;
		}
		String[] subFiles = parentF.list();
		for (int i = 0; i < subFiles.length; i++) {
			getAbsoluteFilesInDirectoryReal(parentF.getAbsolutePath()
					+ System.getProperty("file.separator") + subFiles[i],
					fileSuffix);
		}
	}
}
