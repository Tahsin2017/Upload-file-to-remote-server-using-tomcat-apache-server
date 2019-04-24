<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Upload File</title>
</head>
<body>
	<form action="FileUploadServlet" method ="post" enctype="multipart/form-data" method="post">
 		Select<input type="file" name="file" /><br>
 		<input type="submit" value="upload" />
 	</form> 
 	<%
 		String file_name=(String)request.getParameter("filename");
		if(file_name!=null){
 		out.println(file_name+" File uploaded successfuly");
 }
 	%>
</body>
</html>