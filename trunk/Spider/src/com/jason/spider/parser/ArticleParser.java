package com.jason.spider.parser;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import com.jason.spider.PageDownloader;
import com.jason.spider.msg.Msg;
import com.jason.spider.rule.NewsRule;
import com.jason.spider.rule.Rule;
import com.jason.spider.util.Queue;

public class ArticleParser implements Parser {

	protected static final String lineSign = System.getProperty("line.separator");
	
	protected static final int lineSign_size = lineSign.length();
	
	private static LinkedList<String> linkList = new LinkedList<String>();
	
	private Object lock = new Object();
	
	private  NewsRule newsRule ;
	
	private PageDownloader downloader;
	
	
	
	public ArticleParser(){
		
	}
	
	public ArticleParser(PageDownloader downloader){
		Worker worker = new Worker();
		new Thread(worker).start();
		this.downloader = downloader;
	}
	
	/**
	 * 规则.
	 * 
	 */
	public void setRule(Rule rule){
		this.newsRule = (NewsRule)rule;
	}

	/**
	 * 分析抓取.
	 * 
	 */
	public Msg process() {
		processLink(newsRule.getEntrance());
		if(newsRule.isDownPage()){
			downloader.addUrl(newsRule.getEntrance());
		}
		return null;
	}
	
	/**
	 * 钻取链接，并加入队列.
	 * 
	 * @param url
	 */
	public void processLink(String url){
		try{
			String siteUrl = getLinkUrl(url);
			AndFilter andFilter = new AndFilter();
			org.htmlparser.Parser parser = new org.htmlparser.Parser(siteUrl);
			parser.setEncoding(newsRule.getEncoding());
			parser.reset();
			andFilter.setPredicates(new NodeFilter[]{new NodeClassFilter(LinkTag.class)});
			NodeList list = parser.extractAllNodesThatMatch(andFilter);
			for (int i = 0; i < list.size(); i++) {
				Node node = list.elementAt(i);
				if(node instanceof LinkTag){
					LinkTag linkTag = (LinkTag)node;
					String linkUrl = linkTag.getLink();
					synchronized(lock){
						if(!linkList.contains(linkUrl)){
							linkList.addLast(linkUrl);
							lock.notifyAll();
						}
					}
				}
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 分析链接.
	 * 
	 * @param link
	 * @return
	 */
	private String getLinkUrl(String link) {
		String urlDomaiPattern = "(http://[^/]*?" + "/)(.*?)";

		Pattern pattern = Pattern.compile(urlDomaiPattern,
				Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
		Matcher matcher = pattern.matcher(link);
		String url = "";

		while (matcher.find()) {
			int start = matcher.start(1);
			int end = matcher.end(1);

			url = link.substring(start, end - 1).trim();
		}

		return url;
	}
	
	class Worker implements Runnable{
		
		private boolean isRunning = true;
		
		private ExecutorService executor;
		
		public Worker(){
			executor = Executors.newFixedThreadPool(5);
		}
		
		public void run(){
			while(isRunning){
				String url = null;
				synchronized(lock){
					while(linkList.size() <=0){
						try{
							lock.wait();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					url = linkList.poll();
					ParserWorker worker = new ParserWorker(url);
					executor.execute(worker);
				}
				if(newsRule.isDownPage()){
					downloader.addUrl(url);
				}
			}
		}
	}
	
	
	
	class ParserWorker implements Runnable{
		
		private String url;
		
		public ParserWorker(String url){
			this.url = url;
		}
		
		public void run(){
			String title = processTitle(url);
			String content = processContent(url);
			System.out.println(url + ":" + title);
		}
		
		/**
		 * 分析标题.
		 * 
		 * @param url
		 * @return
		 */
		public String processTitle(String url){
			
			String text = "";
			try {
				org.htmlparser.Parser parser = new org.htmlparser.Parser(url);
				parser.reset();
				ConnectionManager cm = parser.getConnectionManager();
				Hashtable<String,Object> props = new Hashtable<String,Object>();
				props.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
				cm.setRequestProperties(props);
				parser.setEncoding(newsRule.getEncoding());
				NewsTitleNodeVisitor titleVisitor = new NewsTitleNodeVisitor();
				parser.visitAllNodesWith(titleVisitor);
				text = titleVisitor.getContent();
			} catch (Exception e) {
				//e.printStackTrace();
			}
			return text;
		}
		
		/**
		 * 分析正文.
		 * 
		 * @param url
		 * @return
		 */
		public String processContent(String url){
			String text = "";
			try {
				org.htmlparser.Parser parser = new org.htmlparser.Parser(url);
				parser.reset();
				ConnectionManager cm = parser.getConnectionManager();
				Hashtable<String,Object> props = new Hashtable<String,Object>();
				props.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
				cm.setRequestProperties(props);
				parser.setEncoding(newsRule.getEncoding());
				NodeFilter nodeFilter = null;
				if(newsRule.getContentTag() != null){
					AndFilter andFilter = new AndFilter();
					TagNameFilter contentFilter = new TagNameFilter(newsRule.getContentTag());
					Vector<NodeFilter> contentNodeFilterVector = new Vector<NodeFilter>();
					contentNodeFilterVector.add(contentFilter);
					if(newsRule.getContentTagId() != null){
						HasAttributeFilter idAttribute = new HasAttributeFilter("id", newsRule.getContentTagId());
						contentNodeFilterVector.add(idAttribute);
					}
					if(newsRule.getContentTagClass() != null){
						HasAttributeFilter classAttribute = new HasAttributeFilter("class", newsRule.getContentTagClass());
						contentNodeFilterVector.add(classAttribute);
					}
					NodeFilter[] contentNodeFilter = new NodeFilter[contentNodeFilterVector.size()];
					andFilter.setPredicates(contentNodeFilterVector.toArray(contentNodeFilter));
					nodeFilter = andFilter;
				}
				
				StringBuilder builder = new StringBuilder();
				NodeList list = parser.extractAllNodesThatMatch(nodeFilter);
				for (int i = 0; i < list.size(); i++) {
					Node node = list.elementAt(i);
					if (node instanceof Div) {
						Div nodeTag = (Div) node;
						builder.append(nodeTag.getStringText());
					} else if (node instanceof Span) {
						Span nodeTag = (Span) node;
						builder.append(nodeTag.getStringText());
					}
				}
				text = builder.toString();
			} catch (Exception e) {
				//e.printStackTrace();
			}
			return text;
		}
	}
	
	
	/**
	 * 实现自定义访问类.
	 * 
	 * @author lishsh.
	 *
	 */
	class NewsTitleNodeVisitor extends NodeVisitor {
		
		private static final String ID = "id";
		
		private static final String CLASS = "class";
		
		StringBuilder builder = new StringBuilder();
		
		NewsRule rule = ArticleParser.this.newsRule;
		
		public NewsTitleNodeVisitor() {
			super(true, true);
		}

		public void visitTag(Tag tag) {
			
			//存在父节点.先解析父节点.
			if(rule.getParentTag() != null){
				//System.out.println(tag.getText());
				if(tag.getTagName().equalsIgnoreCase(rule.getParentTag())){
					//System.out.println("abc");
					String id = tag.getAttribute(ID);
					String _class = tag.getAttribute(CLASS);
					//System.out.println("_class:" +_class+",class:"+rule.getParentTagClass());
					
					if(id != null && id.equalsIgnoreCase(rule.getParentTagId())){
						NodeList childs = tag.getChildren();
						for(int i=0;i<childs.size();i++){
							Node node = childs.elementAt(i);
							if(node instanceof Tag){
								Tag nodetag = (Tag)node;
								String ruleId = rule.getTitleTagId();
								String ruleClass = rule.getTitleTagClass();
								if(nodetag.getTagName().equalsIgnoreCase(rule.getTitleTag()) 
										&& ruleId != null && nodetag.getAttribute(ID) != null 
										&& ruleId.equalsIgnoreCase(nodetag.getAttribute(ID))){
									builder.append(node.toPlainTextString());
									break;
								}else if(nodetag.getTagName().equalsIgnoreCase(rule.getTitleTag()) 
										&& ruleClass != null && nodetag.getAttribute(CLASS) != null
										&& ruleClass.equalsIgnoreCase(nodetag.getAttribute(CLASS))){
									builder.append(node.toPlainTextString());
									break;
								}else if(nodetag.getTagName().equalsIgnoreCase(rule.getTitleTag())){
									builder.append(node.toPlainTextString());
									break;
								}
							}
						}
					}else if(_class != null && _class.equalsIgnoreCase(rule.getParentTagClass())){
						//System.out.println("123");
						NodeList childs = tag.getChildren();
						
						for(int i=0;i<childs.size();i++){
							Node node = childs.elementAt(i);
							if(node instanceof Tag){
								Tag nodetag = (Tag)node;
								String ruleId = rule.getTitleTagId();
								String ruleClass = rule.getTitleTagClass();
								if(nodetag.getTagName().equalsIgnoreCase(rule.getTitleTag()) 
										&& ruleId != null && nodetag.getAttribute(ID) != null 
										&& ruleId.equalsIgnoreCase(nodetag.getAttribute(ID))){
									builder.append(node.toPlainTextString());
									break;
								}else if(nodetag.getTagName().equalsIgnoreCase(rule.getTitleTag()) 
										&& ruleClass != null && nodetag.getAttribute(CLASS) != null
										&& ruleClass.equalsIgnoreCase(nodetag.getAttribute(CLASS))){
									builder.append(node.toPlainTextString());
									break;
								}else if(nodetag.getTagName().equalsIgnoreCase(rule.getTitleTag())){
									builder.append(node.toPlainTextString());
									break;
								}
							}
							
						}
					}
				}
			}else{
				if(tag.getTagName().equalsIgnoreCase(rule.getTitleTag())){
					String id = tag.getAttribute(ID);
					String _class = tag.getAttribute(CLASS);
					if(id != null && id.equalsIgnoreCase(rule.getTitleTagId())){
						builder.append(tag.toPlainTextString());
					}else if(_class != null && _class.equalsIgnoreCase(rule.getTitleTagClass())){
						builder.append(tag.toPlainTextString());
					}
				}
			}
		}

		public String getContent(){
			return builder.toString();
		}
	}
	
	
	
	

}