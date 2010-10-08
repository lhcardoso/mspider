package com.jason.spider;

public class Lauthor {

	public static void main(String arg[]){
		Component component = new NewsComponent();
		//component.buildRules();
		//component.addUrl("http://gd.news.sina.com.cn/news/2010/09/30/1011159.html");
		//component.addUrl("http://www.javaeye.com/news/17882");
		//component.addUrl("http://cloud.csdn.net/a/20100930/280106.html");
		//component.addUrl("http://news.qq.com/a/20101007/000003.htm");
		component.fire();
	}
}
