package cn.ucas.irsys.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
		String sortOp = request.getParameter("sortOp");
		String curSPage = request.getParameter("curPage");
		
		HttpSession session = request.getSession(true);
		if(request.getMethod() == "POST") {
			session.removeAttribute("sortOp");
			if(queryString!=null)
				session.setAttribute("queryString", queryString);
			
		}
		
		if(request.getMethod() == "GET") {
			queryString = (String)session.getAttribute("queryString");
			System.out.println("===========  " + queryString);
			if(sortOp!=null) {
				sortOp = new String(sortOp.getBytes("iso-8859-1"),"utf-8");
				session.setAttribute("sortOp", sortOp);
			}else {
				sortOp = (String)session.getAttribute("sortOp");
			}
//			queryString = new String(queryString.getBytes("iso-8859-1"), "utf-8");
//			sortOp = new String(sortOp.getBytes("iso-8859-1"),"utf-8");
//			
		}
		
		
		
		int curPage = 1;
		if(curSPage!=null) {
			curPage = Integer.parseInt(curSPage);
		}
		
		if(null == queryString  || queryString.length() <0 || queryString.equals("") ) {
			request.getRequestDispatcher("./index.jsp").forward(request, response);
		}else {
			QueryResult queryResult = aDao.search1(queryString, curPage, GetGProperties.pageSize,sortOp);
			request.setAttribute("queryString", queryString);
			request.setAttribute("ars", queryResult);
			request.getRequestDispatcher("./WEB-INF/showIndex.jsp").forward(request, response);
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
