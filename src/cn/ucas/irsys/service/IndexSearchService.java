package cn.ucas.irsys.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucas.irsys.dao.ArticleIndexDao;
import cn.ucas.irsys.domain.QueryResult;
import cn.ucas.irsys.util.GetGProperties;

@WebServlet("/IndexSearchService")
public class IndexSearchService extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ArticleIndexDao aDao = new ArticleIndexDao();
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf8");
		
		String queryString = request.getParameter("queryString");
		
		
		if(request.getMethod() == "GET") {
			queryString = new String(queryString.getBytes("iso-8859-1"), "utf-8");
		}
		
//		if(queryString == null) {
//			request.getRequestDispatcher(request.getContextPath()).forward(request, response);
//			
//		}
		
		String curSPage = request.getParameter("curPage");
		int curPage = 1;
		if(curSPage!=null) {
			curPage = Integer.parseInt(curSPage);
		}
		
		if(queryString.length() <0 || queryString.equals("") || queryString ==  null) {
			request.getRequestDispatcher("./index.jsp").forward(request, response);
		}else {
			QueryResult queryResult = aDao.search(queryString, curPage, GetGProperties.pageSize);
			request.setAttribute("queryString", queryString);
			request.setAttribute("ars", queryResult);
			request.getRequestDispatcher("./WEB-INF/showIndex.jsp").forward(request, response);
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
