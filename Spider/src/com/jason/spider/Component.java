package com.jason.spider;

import java.util.List;

import com.jason.spider.parser.Parser;
import com.jason.spider.util.Queue;

public abstract class Component {
	
	private static final int POOL_SIZE = 5;
	
	private Spider spider;
	
	private Queue queue;
	
	public Component(){
		this.queue = new Queue();
		spider = new Spider(POOL_SIZE,queue);
	}
	
	public Spider getSpider(){
		return this.spider;
	}
	
	public void addUrl(String url){
		queue.add(url);
	}
	
	public void fire(){
		List<Parser> parsers = buildRules();
		for(Parser parser:parsers){
			this.spider.addParser(parser);
		}
	}
	
	public abstract List<Parser> buildRules();
	
	

}
