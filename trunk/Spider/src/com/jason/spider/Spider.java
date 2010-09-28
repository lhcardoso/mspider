package com.jason.spider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jason.spider.parser.Parser;
import com.jason.spider.util.Queue;

public class Spider {
	
	private List<Parser> parsers = new LinkedList<Parser>();
	
	private ExecutorService executor;
	
	private static final int DEFAULT_POOL_SIZE = 10;
	
	private static final int WORKER_SIZE = 5;
	
	
	
	public Spider(List<String> urls,Parser parser){
		for(String url :urls){
			Queue.add(url);
		}
		this.parsers.add(parser);
		executor = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
	}
	
	public Spider(List<String> urls,List<Parser> parsers){
		for(String url :urls){
			Queue.add(url);
		}
		this.parsers.addAll(parsers);
		executor = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
	}
	
	
	public Spider(String url,Parser parser){
		this(DEFAULT_POOL_SIZE,url,parser);
	}
	
	public Spider(int poolSize,String url,Parser parser){
		executor = Executors.newFixedThreadPool(poolSize);
		Queue.add(url);
		parsers.add(parser);
	}
	
	public Spider(String url,List<Parser> parsers){
		this(DEFAULT_POOL_SIZE,url,parsers);
	}
	
	public Spider(int poolSize,String url,List<Parser> parsers){
		Queue.add(url);
		parsers.addAll(parsers);
	}
	
	public void start(){
		for(Parser parser : parsers){
			for(int i=0;i<WORKER_SIZE;i++){
				Worker worker = new Worker(parser);
				executor.execute(worker);
			}
		}
	}
	
	private class Worker implements Runnable{
		
		private boolean isActivite = true;
		
		private Parser parser;
		
		public Worker(Parser parser){
			this.parser = parser;
			
		}
		

		public void run() {
			while(isActivite){
				String url = Queue.get();
				parser.process(url);
			}
			
		}
	}

}
