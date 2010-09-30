package com.jason.spider.util;

public enum RuleKeyEnum {
	RULE("rule"),
	DOWNLOAD("download"),
	ISDOWNLOAD("isdownload"),
	ENCODING("encoding"),
	TITLE("title"),
	CONTENT("content"),
	PARENT("parent"),
	TAG("tag"),
	ID("id"),
	CLASS("class");
	
	String name;
	
	RuleKeyEnum(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public static RuleKeyEnum getKey(String name){
		if(name.equals("encoding")){
			return ENCODING;
		}else if(name.equals("title")){
			return TITLE;
		}else if(name.equals("content")){
			return CONTENT;
		}else if(name.equals("tag")){
			return TAG;
		}else if(name.equals("id")){
			return ID;
		}else if(name.equals("class")){
			return CLASS;
		}else{
			return RULE;
		}
	}

}
