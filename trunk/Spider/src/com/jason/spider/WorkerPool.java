package com.jason.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerPool {
	
	private static WorkerPool pool;
	
	private ExecutorService executor;
	
	private static final int Default_pool_size = 10;
	
	private WorkerPool(){
		executor = Executors.newFixedThreadPool(Default_pool_size);
	}
	
	public static WorkerPool getInstance(){
		if(pool == null){
			pool = new WorkerPool();
		}
		return pool;
	}
	
	
	public void fire(Worker worker){
		executor.submit(worker);
	}

}
