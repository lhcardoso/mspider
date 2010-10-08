package com.jason.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jason.spider.parser.Parser;
import com.jason.spider.util.Queue;

public class Spider {
	
	
	private ExecutorService executor;
	
	
	
	public Spider(int poolSize){
		executor = Executors.newFixedThreadPool(poolSize);
	}
	
	public void addParser(Parser parser){
		Worker worker = new Worker(parser);
		executor.execute(worker);
	}
	
	
	private class Worker implements Runnable{
		
		private boolean isActivite = true;
		
		private Parser parser;
		
		public Worker(Parser parser){
			this.parser = parser;
			
		}
		
		public void run() {
			parser.process();
		}
	}

}
