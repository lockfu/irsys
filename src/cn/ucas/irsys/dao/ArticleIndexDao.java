package cn.ucas.irsys.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;

import cn.ucas.irsys.domain.Article;
import cn.ucas.irsys.domain.QueryResult;
import cn.ucas.irsys.util.DocumentArticleUtil;
import cn.ucas.irsys.util.LuceneUtil;


public class ArticleIndexDao {
	
	public QueryResult search(String queryString,int curPage,int pageSize) {
		IndexSearcher indexSearcher = null;
		List<Article> lists = new ArrayList<Article>();
		try {
			// 1. Parse queryString to Query Obj
			String[] fields = {"title","content"};
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, fields,LuceneUtil.getAnalyzer() );
			Query query = queryParser.parse(queryString);
			
			// 2. exec search
			indexSearcher = new IndexSearcher(LuceneUtil.getDirectory());
			if(curPage <= 0) {
				curPage = 1;
			}
			int curPageSize = (curPage - 1) * pageSize;
			int sumSize = curPageSize + pageSize;  // 要返回的记录数
			TopDocs topDocs = indexSearcher.search(query, sumSize);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			
			int count = topDocs.totalHits;  // the totalCount of hit keywords
			int endIndex = Math.min(sumSize, topDocs.scoreDocs.length);
			
			
			// ==============标题的和文章的高亮显示============================
			String preTag = "<span style='color: red;'>";
			String postTag = "</span>";
			Formatter formatter = new SimpleHTMLFormatter(preTag, postTag);
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setTextFragmenter(new SimpleFragmenter(255));
			
			// 3. do something address
			for(int i = curPageSize; i<endIndex;i++) {
				int docId = scoreDocs[i].doc;
				Article a = new Article();
				Document doc = indexSearcher.doc(docId);
				
				String dTitle = doc.get("title");
				String hlTitle = highlighter.getBestFragment(LuceneUtil.getAnalyzer(), "title", dTitle);
				
//				int bIndex = dTitle.length();
				int eIndex = dTitle.length() + 150;
				String dContent = doc.get("content");
				if(dContent.length() > (eIndex + 255)) {
					dContent = doc.get("content").substring(eIndex);
				}
//				String dContent = doc.get("content").substring(eIndex);
				
				String hlContent = highlighter.getBestFragment(LuceneUtil.getAnalyzer(), "title", dContent);
				if(hlTitle!=null) {
					doc.getField("title").setValue(hlTitle);
				}
				if(hlContent!=null) {
					doc.getField("content").setValue(hlContent);
				}
				
				a.setId(doc.get("id"));
				a.setTitle(doc.get("title"));
				a.setDate(doc.get("date"));
				a.setUrl(doc.get("url"));
				
				a.setContent(doc.get("content"));
				
				lists.add(a);
			}
			return new QueryResult(curPage,pageSize,count,lists);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			
			if (indexSearcher != null) {
				try {
					indexSearcher.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	
	/**
	 *  进行索引
	 * @param queryString  要查询的字符串
	 * @param first   从结果集中的第几个开始
	 * @param max     最多返回多少条记录
	 * @return     返回记录结果 + 符合条件的总数量
	 */
//	public QueryResult search1(String queryString,int first,int max) {
//		IndexSearcher indexSearcher = null;
//		List<Article> lists = new ArrayList<Article>();
//		try {
//			
//			// 1. Parse queryString to Query Obj
//			String[] fields = {"title","content"};
//			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, fields,LuceneUtil.getAnalyzer() );
//			Query query = queryParser.parse(queryString);
//			
//			// 2. exec search
//			indexSearcher = new IndexSearcher(LuceneUtil.getDirectory());
//			TopDocs topDocs = indexSearcher.search(query, first + max);
//			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//			
//			int count = topDocs.totalHits;  // the totalCount of hit keywords
//			int endIndex = Math.min(first+max, topDocs.scoreDocs.length);
//			
//			// 3. do something address
//			for(int i = first; i<endIndex;i++) {
//				int docId = scoreDocs[i].doc;
//				Article a = new Article();
//				Document doc = indexSearcher.doc(docId);
//				a.setId(doc.get("id"));
//				a.setTitle(doc.get("title"));
//				a.setDate(doc.get("date"));
//				a.setUrl(doc.get("url"));
//				
//				a.setContent(doc.get("content"));
//				
//				lists.add(a);
//			}
//			return new QueryResult(lists, count);
//			
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}finally {
//			
//			if (indexSearcher != null) {
//				try {
//					indexSearcher.close();
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}
//	}
//	
	

	
	
	/**
	 * 删除索引
	 * 
	 * Term ：某字段中出现的某一个关键词（在索引库的目录中）
	 * 
	 * @param id
	 */
	public void delete(String id) {
		try {
			Term term = new Term("id", id);
			
			LuceneUtil.getIndexWriter().deleteDocuments(term); // 删除所有含有这个Term的Document
			LuceneUtil.getIndexWriter().commit(); // 提交更改
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 更新索引
	 * 
	 * @param article
	 */
	public void update(Article article) {
		try {
			Term term = new Term("id",article.getId()); // 一定要使用Lucene的工具类把数字转为字符串！
			Document doc = DocumentArticleUtil.article2Document(article);

			LuceneUtil.getIndexWriter().updateDocument(term, doc); // 更新就是先删除再添加
			LuceneUtil.getIndexWriter().commit(); // 提交更改

			// indexWriter.deleteDocuments(term);
			// indexWriter.addDocument(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
