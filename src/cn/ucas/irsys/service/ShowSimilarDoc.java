package cn.ucas.irsys.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucas.irsys.dao.ArticleIndexDao;
import cn.ucas.irsys.domain.Article;

@WebServlet("/ShowSimilarDoc")
public class ShowSimilarDoc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArticleIndexDao aDao = new ArticleIndexDao();
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mlIds = request.getParameter("mltIds");
		String queryString = (String) request.getSession().getAttribute("queryString");
		List<Article> as = aDao.getMltDocs(mlIds,queryString);
		request.setAttribute("as",as);
		request.setAttribute("queryString",queryString);
		request.getRequestDispatcher("/WEB-INF/similardoc.jsp").forward(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
