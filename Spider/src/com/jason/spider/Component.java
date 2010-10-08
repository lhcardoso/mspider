package com.jason.spider;

import java.util.List;

import com.jason.spider.parser.Parser;
import com.jason.spider.util.Queue;

public abstract class Component {
	
	private static final int POOL_SIZE = 5;
	
	
	private Spider spider;
	
	private Queue queue;
	
	private PageDownloader downloader;
	
	public Component(){
		this.queue = new Queue();
		this.downloader = new PageDownloader(POOL_SIZE);
		spider = new Spider(POOL_SIZE);
	}
	
	public Queue getQueue(){
		return this.queue;
	}
	
	public Spider getSpider(){
		return this.spider;
	}
	
	public PageDownloader getDownloader(){
		return this.downloader;
	}
	
	
	public void fire(){
		List<Parser> parsers = buildRules();
		for(Parser parser:parsers){
			this.spider.addParser(parser);
		}
	}
	
	public abstract List<Parser> buildRules();
	
	

}
