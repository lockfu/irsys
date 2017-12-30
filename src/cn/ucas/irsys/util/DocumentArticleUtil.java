package cn.ucas.irsys.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.junit.Test;

import com.google.gson.Gson;

import cn.ucas.irsys.domain.Article;
import cn.ucas.irsys.domain.MltArticle;

public class DocumentArticleUtil {
	/**
	 * 将Docment转成Article
	 * @param doc
	 * @return
	 */
	public static Article document2Article(Document doc) {
		Article article = new Article();
		article.setId(doc.get("id"));
		article.setTitle(doc.get("title"));
		article.setDate(doc.get("date"));
		article.setUrl(doc.get("url"));
		article.setContent(doc.get("content"));
		return article;
	}
	
	/**
	 * 将Docment转成Article
	 * @param doc
	 * @return
	 */
	public static MltArticle document2ArticleWithMltAs(Document doc,String mltIds) {
		MltArticle mltArticle = new MltArticle();
		Article a = mltArticle.getArticle();
		a.setId(doc.get("id"));
		a.setTitle(doc.get("title"));
		a.setDate(doc.get("date"));
		a.setUrl(doc.get("url"));
		a.setContent(doc.get("content"));
		mltArticle.setArticle(a);
		mltArticle.setMltIds(mltIds);
		return mltArticle;
	}
	
	/**
	 * 将Article转成Docment
	 * @param doc
	 * @return
	 */
	public static Document article2Document(Article article) {
		Document doc = new Document();
		doc.add(new Field("id", article.getId(), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("title", article.getTitle(), Store.YES, Index.ANALYZED));
		doc.add(new Field("date", article.getDate(), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("url", article.getUrl(), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("content", article.getContent(), Store.YES, Index.ANALYZED));
		return doc;
	}
}
