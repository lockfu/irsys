package cn.ucas.irsys.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.similar.MoreLikeThis;
import org.apache.lucene.util.Version;
import org.junit.Test;

import cn.ucas.irsys.domain.Article;
import cn.ucas.irsys.domain.MltArticle;
import cn.ucas.irsys.domain.QueryResult;
import cn.ucas.irsys.util.DocumentArticleUtil;
import cn.ucas.irsys.util.LuceneUtil;


public class ArticleIndexDao {

	/**
	 *  进行索引
	 * @param queryString  要查询的字符串
	 * @param first   从结果集中的第几个开始
	 * @param max     最多返回多少条记录
	 * @return     返回记录结果 + 符合条件的总数量
	 */
	public QueryResult search(String queryString,int curPage,int pageSize,String sortOp) {
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
			Sort sort = null;
			if("_time".equals(sortOp)) {
				sort = new Sort(new SortField[] {new SortField("date", SortField.STRING, true)});  // true 表示降序  false 表示升序
			}else if("_hot".equals(sortOp)){
				// TODO
			}
			TopDocs topDocs;
			if(sort == null) {
				 topDocs = indexSearcher.search(query, sumSize);
				 System.out.println("=========null sort==========");
			}else {
				topDocs = indexSearcher.search(query, null, sumSize, sort);
				System.out.println("==========date sort ===========");
			}
//			TopDocs topDocs = indexSearcher.search(query, sumSize);
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
				
				int eIndex = dTitle.length() + 150;
				String dContent = doc.get("content");
				if(dContent.length() > (eIndex + 255)) {
					dContent = doc.get("content").substring(eIndex);
				}
				
				String hlContent = highlighter.getBestFragment(LuceneUtil.getAnalyzer(), "title", dContent);
				if(hlTitle!=null) {
					doc.getField("title").setValue(hlTitle);
				}
				if(hlContent!=null) {
					doc.getField("content").setValue(hlContent);
				}
				
				
//				a.setId(doc.get("id"));
//				a.setTitle(doc.get("title"));
//				a.setDate(doc.get("date"));
//				a.setUrl(doc.get("url"));
//				
//				a.setContent(doc.get("content"));
//				
//				lists.add(a);
				lists.add(DocumentArticleUtil.document2Article(doc));
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
	
	/**
	 *  进行索引
	 * @param queryString  要查询的字符串
	 * @param first   从结果集中的第几个开始
	 * @param max     最多返回多少条记录
	 * @return     返回记录结果 + 符合条件的总数量
	 */
	public QueryResult search1(String queryString,int curPage,int pageSize,String sortOp) {
		IndexSearcher indexSearcher = null;
		List<MltArticle> lists = new ArrayList<MltArticle>();
		try {
			// 1. Parse queryString to Query Obj
			String[] fields = {"title","content"};
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, fields,LuceneUtil.getAnalyzer() );
			Query query = queryParser.parse(queryString);
			
			IndexReader reader = IndexReader.open(LuceneUtil.getDirectory());
			
			// 设置相似文档
			indexSearcher = new IndexSearcher(reader);
			int numDocs = reader.maxDoc();
			MoreLikeThis mlt = new MoreLikeThis(reader);
			mlt.setFieldNames(new String[] {"title","contet"});
			mlt.setMinTermFreq(1); 
			mlt.setMinDocFreq(1);
			
			// 2. exec search
//			indexSearcher = new IndexSearcher(LuceneUtil.getDirectory());
			if(curPage <= 0) {
				curPage = 1;
			}
			int curPageSize = (curPage - 1) * pageSize;
			int sumSize = curPageSize + pageSize;  // 要返回的记录数
			Sort sort = null;
			if("_time".equals(sortOp)) {
				sort = new Sort(new SortField[] {new SortField("date", SortField.STRING, true)});  // true 表示降序  false 表示升序
			}else if("_hot".equals(sortOp)){
				// TODO
			}
			TopDocs topDocs;
			if(sort == null) {
				 topDocs = indexSearcher.search(query, sumSize);
				 System.out.println("=========null sort==========");
			}else {
				topDocs = indexSearcher.search(query, null, sumSize, sort);
				System.out.println("==========date sort ===========");
			}
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			
			int count = topDocs.totalHits;  // the totalCount of hit keywords
			int endIndex = Math.min(sumSize, topDocs.scoreDocs.length);
			
			// 3. do something address
			for(int i = curPageSize; i<endIndex;i++) {
				int docId = scoreDocs[i].doc;  // 要操作的文档id
				String mltIds = getMltDocIds(mlt, docId, indexSearcher);
				
				Document doc = indexSearcher.doc(docId);
				// 高亮显示
				doc = highLighter(doc, query);
				
				lists.add(DocumentArticleUtil.document2ArticleWithMltAs(doc,mltIds));
				
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
	 * 查找相似文档
	 * @param mlt   
	 * @param docId 要查找的文档id
	 * @param indexSearcher  
	 * @return  返回相似文档的id
	 */
	private String getMltDocIds(MoreLikeThis mlt,int docId,IndexSearcher indexSearcher) {
		try {
			Query mltQuery = mlt.like(docId);
			TopDocs mltDocs = indexSearcher.search(mltQuery, 10);  // 最多返回10条记录
			ScoreDoc[] mltScores = mltDocs.scoreDocs;
			int[] mltIds = new int[mltScores.length];
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<mltScores.length;i++) {   // 保持相似文档的id到mltDocIds中
				if(i == (mltScores.length - 1)) {
					sb.append(mltScores[i].doc+"");
				}else {
					sb.append(mltScores[i].doc+",");
				}
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 根据文档id获取所有文档 并转换成Article返回
	 * @param mltIds
	 * @param queryString
	 * @return
	 */
	public List<Article> getMltDocs(String mltIds,String queryString) {
		try {
			List<Article> as = new ArrayList<Article>();
			String[] fields = {"title","content"};
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, fields,LuceneUtil.getAnalyzer() );
			Query query = queryParser.parse(queryString);
			IndexSearcher indexSearcher = new IndexSearcher(LuceneUtil.getDirectory());
			String[] mmIds = mltIds.split(",");
			for(String mid : mmIds) {
				int docid = Integer.parseInt(mid);
				Document doc = indexSearcher.doc(docid);
				doc = highLighter(doc, query);
				as.add(DocumentArticleUtil.document2Article(doc));
			}
			return as;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	/**
	 * 文档的高亮显示
	 * @param doc  被操作的文档
	 * @param query 
	 * @return  返回高亮操作后的文档
	 */
	private Document highLighter(Document doc,Query query) {
		try {
			String preTag = "<span style='color: red;'>";
			String postTag = "</span>";
			Formatter formatter = new SimpleHTMLFormatter(preTag, postTag);
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setTextFragmenter(new SimpleFragmenter(255));
			
			String dTitle = doc.get("title");
			String hlTitle = highlighter.getBestFragment(LuceneUtil.getAnalyzer(), "title", dTitle);
			
			int eIndex = dTitle.length() + 150;
			String dContent = doc.get("content");
			if(dContent.length() > (eIndex + 255)) {
				dContent = doc.get("content").substring(eIndex);
			}
			
			String hlContent = highlighter.getBestFragment(LuceneUtil.getAnalyzer(), "title", dContent);
			if(hlTitle!=null) {
				doc.getField("title").setValue(hlTitle);
			}
			if(hlContent!=null) {
				doc.getField("content").setValue(hlContent);
			}
			return doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
}
