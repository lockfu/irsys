package cn.ucas.irsys.domain;


/**
 * 包装的文章类 包含文章和相似文档的id
 * @author lockjk
 *
 */
public class MltArticle {
	private Article article = new Article();
	private String mltIds;
	
	
	public Article getArticle() {
		return article;
	}
	public void setArticle(Article article) {
		this.article = article;
	}
	public String getMltIds() {
		return mltIds;
	}
	public void setMltIds(String mltIds) {
		this.mltIds = mltIds;
	}
	
	
	
}
