package cn.ucas.irsys.domain;


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
