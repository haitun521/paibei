package com.haitun.pb.utils;


import java.io.ByteArrayOutputStream;

import java.io.InputStream;

public class StreamTool {
	/**
	 *
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] read(InputStream inputStream) throws Exception {
		// TODO Auto-generated method stub
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		byte[] buffer=new byte[1024];
		int len=0;
		while((len=inputStream.read(buffer))!=-1){
			stream.write(buffer, 0, len);
		}
		inputStream.close();
		return stream.toByteArray();
	}

}
