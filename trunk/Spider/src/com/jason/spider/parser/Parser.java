package com.jason.spider.parser;

import com.jason.spider.msg.Msg;
import com.jason.spider.rule.Rule;

public interface Parser {
	
	public void setRule(Rule rule);
	
	public Msg process(String url);

}
