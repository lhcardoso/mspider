package com.jason.spider.util;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {

	private static LinkedList<String> queue = new LinkedList<String>();

	private static ReentrantLock lock = new ReentrantLock();
	
	
	private static Condition notEmpty = lock.newCondition();
	

	public static  void add(String t) {
		if (queue.contains(t)) {
			return;
		}
		queue.addLast(t);
		
	}

	public  static String get() {
		lock.lock();
		try{
			if(queue.size()==0){
				notEmpty.await();
			}
			String url = queue.poll();
			return url;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			lock.unlock();
		}
		
	}

	public static boolean isEmpty() {
		return queue.isEmpty();
	}

}
