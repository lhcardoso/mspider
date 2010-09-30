package com.jason.spider;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jason.spider.parser.ArticleParser;
import com.jason.spider.parser.Parser;
import com.jason.spider.rule.NewsRule;
import com.jason.spider.rule.Rule;
import com.jason.spider.util.Constance;
import com.jason.spider.util.RuleKeyEnum;

/***
 * 新闻相关组件
 * 
 * @author lishsh.
 *
 */
public class NewsComponent extends Component{
	
	private static final int BYTE_BUFFER = 1024;
	
	private static final String news_rule_path = Constance.RULE_PATH +"/news";
	
	private List<NewsRule> rules;
	
	private List<File> ruleFiles;
	
	private List<Parser> parsers;
	
	public NewsComponent(){
		rules = new CopyOnWriteArrayList<NewsRule>();
		ruleFiles = new CopyOnWriteArrayList<File>();
		parsers = new CopyOnWriteArrayList<Parser>();
	}
	
	/**
	 * 构建相关规则.
	 * 
	 */
	public List<Parser> buildRules(){
		readPath(news_rule_path);
		parseFiles();
		for(Rule rule:rules){
			Parser parser = new ArticleParser();
			parser.setRule(rule);
			parsers.add(parser);
		}
		return parsers;
	}
	
	/**
	 * 读取路径规则文件.
	 * 
	 * 
	 * @param siteCode
	 * @return
	 * @throws Exception
	 */
	private void readPath(String path) {
		try {
			File file = new File(path);
			if(file.isDirectory()){
				File[] files = file.listFiles(new FileFilter(){
					public boolean accept(File sub){
						if(sub.isDirectory() || ((sub.isFile() && sub.getName().endsWith(".xml")))){
							return true;
						}else{
							return false;
						}
					}
				});
				for(File f:files){
					if(f.isFile()){
						ruleFiles.add(f);
					}else{
						readPath(f.getPath());
					}
				}
			}else{
				ruleFiles.add(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 转换规则文件-对象.
	 * 
	 */
	private void parseFiles(){
		for(File file:ruleFiles){
			try{
				NewsRule rule = new NewsRule();
				SAXReader saxReader = new SAXReader();
	            Document document = saxReader.read(file);
	            Element rootNode = document.getRootElement();
	            String encoding = rootNode.element(RuleKeyEnum.ENCODING.getName()).getText();
	            rule.setEncoding(encoding);
	            Element title = rootNode.element(RuleKeyEnum.TITLE.getName());
	            Element titleTag = title.element(RuleKeyEnum.TAG.getName());
	            Element titleId = title.element(RuleKeyEnum.ID.getName());
	            Element titleClass = title.element(RuleKeyEnum.CLASS.getName());
	            rule.setTitleTag(titleTag.getText());
	            if(titleId != null){
	            	rule.setTitleTagId(titleId.getText());
	            }
	            if(titleClass != null){
	            	rule.setTitleTagClass(titleClass.getText());
	            }
	            Element content = rootNode.element(RuleKeyEnum.CONTENT.getName());
	            Element contentTag = content.element(RuleKeyEnum.TAG.getName());
	            Element contentId = content.element(RuleKeyEnum.ID.getName());
	            Element contentClass = content.element(RuleKeyEnum.CLASS.getName());
	            rule.setContentTag(contentTag.getText());
	            if(contentId != null){
	            	rule.setContentTagId(contentId.getText());
	            }
	            if(contentClass != null){
	            	rule.setContentTagClass(contentClass.getText());
	            }
	            Element parent = rootNode.element(RuleKeyEnum.PARENT.getName());
	            if(parent != null){
	            	Element parentTag = parent.element(RuleKeyEnum.TAG.getName());
	 	            Element parentId = parent.element(RuleKeyEnum.ID.getName());
	 	            Element parentClass = parent.element(RuleKeyEnum.CLASS.getName());
	 	            rule.setParentTag(parentTag.getText());
	 	            if(parentId != null){
	 	            	rule.setParentTagId(parentId.getText());
	 	            }
	 	            if(parentClass != null){
	 	            	rule.setParentTagClass(parentClass.getText());
	 	            }
	            }
	           
	            rules.add(rule);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	

}
