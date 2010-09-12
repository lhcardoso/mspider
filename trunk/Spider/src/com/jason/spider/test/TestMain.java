package com.jason.spider.test;

public class TestMain extends Thread{
	
	
	
	public static void main(String[] arg){
		int len =4;
		System.out.println(Integer.toBinaryString(len));
		byte[] mybytes = new byte[4];
		mybytes[3] = (byte) (0xff & len);
		mybytes[2] = (byte) ((0xff00 & len) >> 8);
		mybytes[1] = (byte) ((0xff0000 & len) >> 16);
		mybytes[0] = (byte) ((0xff000000 & len) >> 24);
		
	}

}
