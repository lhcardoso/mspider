package com.jason.spider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jason.spider.parser.Parser;
import com.jason.spider.util.Queue;

public class Spider {
	
	private List<Parser> parsers = new LinkedList<Parser>();
	
	
	private static final int DEFAULT_POOL_SIZE = 10;
	
	private static final int WORKER_SIZE = 5;
	
	
	public Spider(String url,Parser parser){
		this(DEFAULT_POOL_SIZE,url,parser);
	}
	
	public Spider(int poolSize,String url,Parser parser){
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
			Worker worker = new Worker(parser);
			WorkerPool pool = WorkerPool.getInstance();
			pool.fire(worker);
		}
	}

}
