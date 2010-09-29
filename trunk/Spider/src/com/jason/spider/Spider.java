package com.jason.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jason.spider.parser.Parser;
import com.jason.spider.util.Queue;

public class Spider {
	
	
	private ExecutorService executor;
	
	
	private Object lock = new Object();
	
	private Queue queue;
	
	
	public Spider(int poolSize,Queue queue){
		executor = Executors.newFixedThreadPool(poolSize);
		this.queue = queue;
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
			while(isActivite){
				String url = queue.get();
				parser.process(url,queue);
			}
			
		}
	}

}
