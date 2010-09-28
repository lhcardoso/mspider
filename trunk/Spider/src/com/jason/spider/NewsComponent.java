package com.jason.spider;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import com.jason.spider.parser.ArticleParser;
import com.jason.spider.parser.Parser;
import com.jason.spider.rule.NewsRule;
import com.jason.spider.rule.Rule;
import com.jason.spider.rule.SinaRule;
import com.jason.spider.util.Constance;

/***
 * 新闻相关组件
 * 
 * @author lishsh.
 *
 */
public class NewsComponent implements Component{
	
	private static final String news_rule_path = Constance.RULE_PATH +"/news";
	
	/**
	 * 构建相关规则.
	 * 
	 */
	public void buildRules(){
		
	}
	
	/**
	 * 用FileChannel读取嵌套码文件内容.
	 * 
	 * @param siteCode
	 * @return
	 * @throws Exception
	 */
	private String readPath(String path) {
		String code = "获取嵌入码配置文件出错，请确保site_code.21cn位于classes目录";
		try {
			File file = new File(path);
			if(file.isDirectory()){
				File[] files = file.listFiles(new FileFilter(){
					public boolean accept(File subFile){
						if(subFile.getName().lastIndexOf(".rule") != -1){
							return true;
						}else{
							return false;
						}
					}
				});
				/*FileInputStream fileOut = new FileInputStream(conf);
				FileChannel fileChannel = fileOut.getChannel();
				StringBuilder sb = new StringBuilder();
				ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_BUFFER);
				while (fileChannel.read(byteBuffer) != -1) {
					byteBuffer.flip();
					while (byteBuffer.hasRemaining()) {
						sb.append((char) byteBuffer.get());
					}
					byteBuffer.clear();
				}
				code = sb.toString();*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return code;
	}
	
	public static void main(String arg[]){
		List<String> urls = new LinkedList<String>();
		List<Rule> rules = new LinkedList<Rule>();
		List<Parser> parsers = new LinkedList<Parser>();
		
		//163
		String _163url = "http://news.163.com/10/0905/01/6FPHM79P000146BD.html";
		NewsRule _163Rule = new NewsRule();
		_163Rule.setTitleTag("h1");
		_163Rule.setTitleTagId("h1title");
		_163Rule.setEncoding("gb2312");
		_163Rule.setContentTag("div");
		_163Rule.setContentTagId("endText");
		urls.add(_163url);
		rules.add(_163Rule);
		
		//新浪.
		/*String sinaurl = "http://gd.news.sina.com.cn/news/2010/09/04/989563.html";
	
		Rule sinaRule = new SinaRule();
		sinaRule.setEncoding("gb2312");
		sinaRule.setContentTag("div");
		sinaRule.setContentTagId("artibody1");
		sinaRule.setTitleTag("h1");
		sinaRule.setTitleTagId("artibodyTitle");
		urls.add(sinaurl);
		rules.add(sinaRule);*/
		
		//csdn博客
		/*String csdnBlogUrl = "http://blog.csdn.net/cping1982/archive/2010/08/14/5811779.aspx";
		Rule csdnBlogRule = new CsdnBlogRule();
		csdnBlogRule.setEncoding("utf-8");
		csdnBlogRule.setContentTag("div");
		csdnBlogRule.setContentTagClass("blogstory");
		urls.add(csdnBlogUrl);
		rules.add(csdnBlogRule);*/
		
		for(Rule rule:rules){
			Parser parser = new ArticleParser();
			parser.setRule(rule);
			parsers.add(parser);
		}
		Spider spider = new Spider(urls,parsers);
		spider.start();
	}

}
