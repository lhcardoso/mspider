package com.jason.spider;

import com.jason.spider.parser.ArticleParser;
import com.jason.spider.parser.Parser;
import com.jason.spider.rule.Rule;
import com.jason.spider.rule.SinaRule;

public class Laucher {
	
	public static void main(String arg[]){
		//新浪.
		String url = "http://gd.news.sina.com.cn/news/2010/09/04/989563.html";
		Rule rule = new SinaRule();
		rule.setContentTag("div");
		rule.setContentTagId("artibody1");
		
		Parser parser = new ArticleParser();
		parser.setRule(rule);
		Spider spider = new Spider(url,parser);
		spider.start();
	}

}
