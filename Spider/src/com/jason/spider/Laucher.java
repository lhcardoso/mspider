package com.jason.spider;

import java.util.LinkedList;
import java.util.List;

import com.jason.spider.parser.ArticleParser;
import com.jason.spider.parser.Parser;
import com.jason.spider.rule.CsdnBlogRule;
import com.jason.spider.rule.Rule;
import com.jason.spider.rule.SinaRule;
import com.jason.spider.rule._163Rule;

public class Laucher {
	
	public static void main(String arg[]){
		List<String> urls = new LinkedList<String>();
		List<Rule> rules = new LinkedList<Rule>();
		List<Parser> parsers = new LinkedList<Parser>();
		
		//163
		/*String _163url = "http://news.163.com/10/0905/01/6FPHM79P000146BD.html";
		Rule _163Rule = new _163Rule();
		_163Rule.setEncoding("gb2312");
		_163Rule.setContentTag("div");
		_163Rule.setContentTagId("endText");
		urls.add(_163url);
		rules.add(_163Rule);*/
		
		//新浪.
		String sinaurl = "http://gd.news.sina.com.cn/news/2010/09/04/989563.html";
	
		Rule sinaRule = new SinaRule();
		sinaRule.setEncoding("gb2312");
		sinaRule.setContentTag("div");
		sinaRule.setContentTagId("artibody1");
		sinaRule.setTitleTag("h1");
		sinaRule.setTitleTagId("artibodyTitle");
		urls.add(sinaurl);
		rules.add(sinaRule);
		
		//csdn博客
		/*String csdnBlogUrl = "http://blog.csdn.net/cping1982/archive/2010/08/14/5811779.aspx";
		Rule csdnBlogRule = new CsdnBlogRule();
		csdnBlogRule.setEncoding("utf-8");
		csdnBlogRule.setContentTag("div");
		csdnBlogRule.setContentTagClass("blogstory");
		urls.add(csdnBlogUrl);
		rules.add(csdnBlogRule);*/
		
		for(Rule rule:rules){
			Parser parser = new ArticleParser();
			parser.setRule(rule);
			parsers.add(parser);
		}
		Spider spider = new Spider(urls,parsers);
		spider.start();
	}

}
