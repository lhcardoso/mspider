package com.jason.spider.test;

import java.math.BigDecimal;

public class BankAccount {
	
	private BigDecimal balance;
	
	public BankAccount(BigDecimal balance){
		this.balance = balance;
	}
	
	public synchronized void deposit(BigDecimal amount){
		balance = balance.add(amount);
	}
	
	public synchronized void withdraw(BigDecimal amount){
		balance = balance.subtract(amount);
	}
	
	public BigDecimal getBalance(){
		return balance;
	}
	
	
	public static void main(String arg[]) throws Exception{
		BankAccount a = new BankAccount(new BigDecimal(1000));
		Thread depositThread = new Thread(new depositWorker(a,new BigDecimal(100)),"depositThread");
		Thread withdrawThread = new Thread(new withdrawWorker(a,new BigDecimal(100)),"withdrawThread");
		depositThread.start();
		withdrawThread.start();
		depositThread.join();
		withdrawThread.join();
		System.out.println(a.getBalance().longValue());
	}
	
	static class depositWorker implements Runnable{
		
		BankAccount account;
		
		BigDecimal amount;
		
		public depositWorker(BankAccount account,BigDecimal amount){
			this.account = account;
			this.amount = amount;
		}
		
		public void run(){
			for(int i = 0;i < 100000;i++){
				account.deposit(amount);
			}
		}
	}
	
	static class withdrawWorker implements Runnable{
		private BankAccount account;
		
		private BigDecimal amount;
		
		public withdrawWorker(BankAccount account,BigDecimal amount){
			this.account = account;
			this.amount = amount;
		}
		
		public void run(){
			for(int i = 0;i < 100000;i++){
				account.withdraw(amount);
			}
		}
	}

}
