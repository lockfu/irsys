<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

</head>
<body>
    <form action="<%=request.getContextPath() %>/IndexSearchService" method="post">
    	<input type="text" name="queryString" size="30" /> 
    	<input type="submit" value="Search" />
    </form>
    <span>本次共搜索到：${ars.count }条记录</span>
    
	<c:forEach items="${ars.list }" var="a">
		<div style="width: 100%;height: 100px;">
			<div style="height: 20px;">
				<a href=${a.url }>${a.title }</a>
			</div>
			<div style="height: 80px;overflow: hidden;">
				${a.content }
			</div>
		</div>
	</c:forEach>
	<br />
		<a href="<%=request.getContextPath() %>/IndexSearchService?queryString=${queryString}&curPage=${(ars.currentPage - 1) <= 0 ? 1 : (ars.currentPage - 1)}">上一页</a>
		&nbsp;&nbsp;&nbsp;
		<c:forEach begin="${ars.beginPageIndex }" end="${ars.endPageIndex }" var="i">
			<a href="<%=request.getContextPath() %>/IndexSearchService?queryString=${queryString}&curPage=${i}">${i }</a>&nbsp;&nbsp;
		</c:forEach>
		&nbsp;&nbsp;&nbsp;
		<a href="<%=request.getContextPath() %>/IndexSearchService?queryString=${queryString}&curPage=${(ars.currentPage + 1) > ars.pageCount ? ars.pageCount : (ars.currentPage + 1)}">下一页</a>
</body>
</html>