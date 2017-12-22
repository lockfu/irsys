package cn.ucas.irsys.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

import cn.ucas.irsys.domain.Article;

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
