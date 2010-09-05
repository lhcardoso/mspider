package com.jason.spider;

import com.jason.spider.parser.Parser;
import com.jason.spider.util.Queue;

public class Worker implements Runnable{
	
	
	private Parser parser;
	
	public Worker(Parser parser){
		this.parser = parser;
	}

	public void run() {
		String url = Queue.get();
		parser.process(url);
	}
	
	

}
