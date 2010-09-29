package com.jason.spider;

public class Lauthor {

	public static void main(String arg[]){
		Component component = new NewsComponent();
		component.buildRules();
		component.addUrl("http://gd.news.sina.com.cn/news/2010/09/29/1009606.html");
		component.fire();
	}
}
