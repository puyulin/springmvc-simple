package com.cn.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtil {

	public static byte[] obj2byte(Object obj) throws IOException{
		ByteArrayOutputStream byt=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(byt);
		oos.writeObject(obj);
		byte[] bytes=byt.toByteArray();
		System.out.println(bytes);
		return bytes;
	}
	
	public static <T>  T byte2obj(byte[] bytes) throws ClassNotFoundException, IOException{
		ByteArrayInputStream byteInt=new ByteArrayInputStream(bytes);
		ObjectInputStream objInt=new ObjectInputStream(byteInt);
		T t=(T)objInt.readObject();
		return t;
		
	}
}
