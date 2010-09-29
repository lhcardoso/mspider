package com.jason.spider;

public class Lauthor {

	public static void main(String arg[]){
		Component component = new NewsComponent();
		component.buildRules();
		component.addUrl("http://www.javaeye.com/news/17882");
		component.fire();
	}
}
