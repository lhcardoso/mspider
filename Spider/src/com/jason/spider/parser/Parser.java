package com.jason.spider.parser;

import com.jason.spider.msg.Msg;
import com.jason.spider.rule.Rule;
import com.jason.spider.util.Queue;

public interface Parser {
	
	public void setRule(Rule rule);
	
	public Msg process(String url,Queue queue);

}
