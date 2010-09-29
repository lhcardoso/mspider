package com.jason.spider.parser;

import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;

import com.jason.spider.msg.Msg;
import com.jason.spider.rule.NewsRule;
import com.jason.spider.rule.Rule;
import com.jason.spider.util.Queue;

public class ArticleParser implements Parser {

	protected static final String lineSign = System.getProperty("line.separator");
	protected static final int lineSign_size = lineSign.length();
	
	private NewsRule newsRule;
	
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
	public Msg process(String url,Queue queue) {
		//System.out.println(url);
		String title = processTitle(url);
		String content = processContent(url);
		System.out.println(title);
		processLink(url,queue);
		return null;
	}
	
	/**
	 * 钻取链接，并加入队列.
	 * 
	 * @param url
	 */
	public void processLink(String url,Queue queue){
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
					queue.add(linkUrl);
				}
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
		
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
			parser.setEncoding(newsRule.getEncoding());
			NodeFilter nodeFilter = null;
			if(newsRule.getTitleTag() != null){
				AndFilter andFilter = new AndFilter();
				TagNameFilter titleFilter = new TagNameFilter(newsRule.getTitleTag());
				Vector<NodeFilter> titleNodeFilterVector = new Vector<NodeFilter>();
				titleNodeFilterVector.add(titleFilter);
				if(newsRule.getTitleTagId() != null){
					HasAttributeFilter idAttribute = new HasAttributeFilter("id", newsRule.getTitleTagId());
					titleNodeFilterVector.add(idAttribute);
				}
				if(newsRule.getTitleTagClass() != null){
					HasAttributeFilter classAttribute = new HasAttributeFilter("class", newsRule.getTitleTagClass());
					titleNodeFilterVector.add(classAttribute);
				}
				if(newsRule.getParentTag() != null){
					TagNameFilter parentFilter = new TagNameFilter(newsRule.getParentTag());
					titleNodeFilterVector.add(parentFilter);
					if(newsRule.getParentTagId() != null){
						HasAttributeFilter parentIdAttribute = new HasAttributeFilter("id", newsRule.getParentTagId());
						titleNodeFilterVector.add(parentIdAttribute);
					}
					if(newsRule.getParentTagClass() != null){
						HasAttributeFilter parentClassAttribute = new HasAttributeFilter("class", newsRule.getParentTagClass());
						titleNodeFilterVector.add(parentClassAttribute);
					}
				}
				NodeFilter[] titleNodeFilter = new NodeFilter[titleNodeFilterVector.size()];
				andFilter.setPredicates(titleNodeFilterVector.toArray(titleNodeFilter));
				nodeFilter = andFilter;
			}
			
			StringBuilder builder = new StringBuilder();
			NodeList list = parser.extractAllNodesThatMatch(nodeFilter);
			if(list.size() >1){
				System.out.println("size >1:"+url);
				for (int i = 0; i < list.size(); i++){
					Node node = list.elementAt(i);
					System.out.println(node.toString());
					Node parent = node.getParent();
					System.out.println(parent.toString());
					if (parent instanceof Div){
						System.out.println("parent div");
						Div parentTag = (Div) parent;
						String id = parentTag.getAttribute("id");
						String clas = parentTag.getAttribute("class");
						if(parentTag.getRawTagName().equals(newsRule.getParentTag())
								&&((id != null && id.equals(newsRule.getParentTagId()))
								||(clas!=null && clas.equals(newsRule.getParentTagClass()))
								)){
							System.out.println(parentTag.getRawTagName()+id+clas);
							
							if (node instanceof Div) {
								Div nodeTag = (Div) node;
								builder.append(nodeTag.getStringText());
							} else if (node instanceof Span) {
								Span nodeTag = (Span) node;
								builder.append(nodeTag.getStringText());
							}else if (node instanceof HeadingTag) {
								HeadingTag nodeTag = (HeadingTag) node;
								builder.append(nodeTag.getStringText());
							}
							break;
						}
					}else if (parent instanceof Span) {
						System.out.println("parent span");
						Span parentTag = (Span) parent;
						String id = parentTag.getAttribute("id");
						String clas = parentTag.getAttribute("class");
						if(parentTag.getRawTagName().equals(newsRule.getParentTag())
								&&((id != null && id.equals(newsRule.getParentTagId()))
								||(clas!=null && clas.equals(newsRule.getParentTagClass()))
								)){
							if (node instanceof Div) {
								Div nodeTag = (Div) node;
								builder.append(nodeTag.getStringText());
							} else if (node instanceof Span) {
								Span nodeTag = (Span) node;
								builder.append(nodeTag.getStringText());
							}else if (node instanceof HeadingTag) {
								HeadingTag nodeTag = (HeadingTag) node;
								builder.append(nodeTag.getStringText());
							}
							break;
						}
					}else if (parent instanceof HeadingTag) {
						System.out.println("parent heading");
						HeadingTag parentTag = (HeadingTag) parent;
						String id = parentTag.getAttribute("id");
						String clas = parentTag.getAttribute("class");
						if(parentTag.getRawTagName().equals(newsRule.getParentTag())
								&&((id != null && id.equals(newsRule.getParentTagId()))
								||(clas!=null && clas.equals(newsRule.getParentTagClass()))
								)){
							if (node instanceof Div) {
								Div nodeTag = (Div) node;
								builder.append(nodeTag.getStringText());
							} else if (node instanceof Span) {
								Span nodeTag = (Span) node;
								builder.append(nodeTag.getStringText());
							}else if (node instanceof HeadingTag) {
								HeadingTag nodeTag = (HeadingTag) node;
								builder.append(nodeTag.getStringText());
							}
							break;
						}
					}
				}
			}else{
				System.out.println("size=1"+url);
				for(int i = 0; i < list.size(); i++){
					Node node = list.elementAt(i);
					System.out.println("node:"+node.toString());
					Node parent = node.getParent();
					System.out.println("parent"+parent.toString());
					if (node instanceof Div) {
						Div nodeTag = (Div) node;
						builder.append(nodeTag.getStringText());
					} else if (node instanceof Span) {
						Span nodeTag = (Span) node;
						builder.append(nodeTag.getStringText());
					}else if (node instanceof HeadingTag) {
						HeadingTag nodeTag = (HeadingTag) node;
						builder.append(nodeTag.getStringText());
					}
				}
			}
			
			text = builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
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

}