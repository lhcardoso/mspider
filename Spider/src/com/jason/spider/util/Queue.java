package com.jason.spider.util;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {

	private LinkedList<String> queue = new LinkedList<String>();

	//private static ReentrantLock lock = new ReentrantLock();
	
	private Object lock = new Object();
	
	//private static Condition notEmpty = lock.newCondition();
	

	public  void add(String t) {
		synchronized(lock){
			try{
				if (queue.contains(t)) {
					return;
				}
				queue.addLast(t);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

	public   String get() {
		synchronized(lock){
			try{
				if(queue.size()==0){
					lock.wait();
				}
				String url = queue.poll();
				return url;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}

	public  boolean isEmpty() {
		return queue.isEmpty();
	}

}
