package com.jason.spider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PageDownloader {
	
	private static final int BUFFER_SIZE = 1024;
	
	private final LinkedList<String> urls = new LinkedList<String>();
	
	private ExecutorService executor;
	
	private boolean running = true;
	
	private Object lock = new Object();
	
	
	private String outputPath = "G:/mspider/download";

	
	public PageDownloader(int downerSize){
		executor = Executors.newFixedThreadPool(downerSize);
		fireDownload();
	}
	
	public void setPath(String path){
		this.outputPath = path;
	}
	
	public void addUrl(String url){
		synchronized(lock){
			urls.add(url);
			lock.notifyAll();
		}
		
	}
	
	public void fireDownload(){
		Worker worker = new Worker();
		executor.execute(worker);
	}
	
	
	class Worker implements Runnable{
		
		public void run(){
			while(running){
				try{
					synchronized(lock){
						while(urls.size() <=0){
							lock.wait();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				String url = urls.poll();
				synchronized(url){
					HttpURLConnection connect=null;
			        BufferedInputStream in=null;
			        FileOutputStream file=null;
			        byte[] buf=new byte[BUFFER_SIZE];
			        int size=0;
			        try{
			        	   File outpath = new File(outputPath);
			        	   if(!outpath.exists()){
			        		   outpath.mkdirs();
			        	   }
			        	   String fileName = System.currentTimeMillis() + ".htm";
			               URL downurl=new URL(url);  
			               connect=(HttpURLConnection) downurl.openConnection();
			               connect.connect();   
			               in=new BufferedInputStream(connect.getInputStream()); 
			               file=new FileOutputStream(outputPath+"/" +fileName); 
			               while((size=in.read(buf))!=-1)
			               {
			                   file.write(buf,0,size);
			               }           
			        } catch (MalformedURLException e){
			            e.printStackTrace();
			        }catch (IOException e){
			            e.printStackTrace();
			        }finally{
			            try{
			                  file.close();
			                  in.close();
			            }catch (IOException e){
			                e.printStackTrace();
			            }
			            connect.disconnect();
			        }
				}
			}
			
		}
	}
	
	
	

}
