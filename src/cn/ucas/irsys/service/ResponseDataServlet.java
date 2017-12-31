package cn.ucas.irsys.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import cn.ucas.irsys.domain.Article;
import cn.ucas.irsys.domain.HotArticle;

@WebServlet("/ResponseDataServlet")
public class ResponseDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		String str = "http://news.baidu.com/";
		Document doc = Jsoup.connect(str).timeout(0).get();
		Elements eles = doc.select("li[class=bold-item] > a");
		Iterator<Element> it = eles.iterator();
		List<HotArticle> hotA = new ArrayList<HotArticle>();
		int count = 0;
		while(it.hasNext()) {
			Element e = it.next();
			if(e.text() == null || e.text().length()<0 || "".equals(e.text())) {
				continue;
			}
			HotArticle hot = new HotArticle();
			String url = e.absUrl("href");
			hot.setUrl(url);
			hot.setTitle(e.text());
			hotA.add(hot);
			if(count > 6) {
				break;
			}
//			System.out.println("======="+e.text()+"==========" + " ================"+url+"==============");
		}
		Gson gson = new Gson();
		String gString = gson.toJson(hotA);
//		System.out.println(gString);
		out.print(gString);
		out.flush();
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
