<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	setTimeout(aa(), 0);
	
	function aa(){
		var xhr = new XMLHttpRequest();
		var url = "<%=request.getContextPath()%>/ResponseDataServlet";
		//alert(url);
		
		xhr.open("get", url, true);
		xhr.onreadystatechange = function() {
		    if (xhr.readyState == 4 && xhr.status == 200) {
		        //获取后台传递过来的字符串并转换为json
		        var responseJson=JSON.parse(xhr.responseText);
		        for(var i = 0;i<responseJson.length;i++){
		        	var odoc = document.getElementById("d"+(i+1));
		        	var dContent = "<a href=" + responseJson[i].url+ ">" +responseJson[i].title + "</a>";
		        	odoc.innerHTML = dContent;
		        }
		    }
		};
		xhr.send(null);
		
	}
	
</script>
<style type="text/css">
	div{
		float: left;
	}
</style>
</head>
<body>
	<center>
	    <form action="<%=request.getContextPath() %>/IndexSearchService" method="post">
	    	<input type="text" name="queryString" size="30" /> 
	    	<input type="submit" value="Search" />
	    </form>
    </center>
    
    <div id="d1" style="width: 100px; height: 150px; border: 1px  solid red; margin-top: 50px; margin-left: 80px;">
    	
    </div>
    <div id="d2" style="width: 100px; height: 150px; border: 1px  solid red; margin-top: 50px; margin-left: 80px; ">
    	
    </div>
    <div id="d3" style="width: 100px; height: 150px; border: 1px  solid red; margin-top: 50px; margin-left: 80px;">
    	
    </div>
    <div id="d4" style="width: 100px; height: 150px; border: 1px  solid red; margin-top: 50px; margin-left: 80px;">
    	
    </div>
    <div id="d5" style="width: 100px; height: 150px; border: 1px  solid red; margin-top: 50px; margin-left: 80px;">
    	
    </div>
    <div id="d6" style="width: 100px; height: 150px; border: 1px  solid red; margin-top: 50px; margin-left: 80px;">
    	
    </div>
</body>
</html>