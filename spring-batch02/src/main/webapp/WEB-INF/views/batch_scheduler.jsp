<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>배치스케줄 관리</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/batch_scheduler.css">
	<meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<!-- Navigation -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" style="border-radius: 0; margin-bottom: 10px;">
		<div class="container">
			<span class="navbar-brand">배치 스케줄 관리</span>
			<!--  
			<div class="collapse navbar-collapse" id="navbarResponsive">
				<ul class="navbar-nav ml-auto">
					<li class="nav-item active"><a class="nav-link" href="/etc">홈
							<span class="sr-only">(current)</span>
					</a></li>
					<li class="nav-item"><a class="nav-link"
						href="/join/login.jsp">로그인</a></li>
					<li class="nav-item"><a class="nav-link"
						href="/join/register.jsp">회원가입</a></li>
				</ul>
			</div>
			-->
		</div>
	</nav>
	<!-- Page Content -->
	<div class="container" style="height:80%;">
		<div class="row">
			<div class="col-lg-3">
				<h3 class="my-4 text-center">Order by</h3>
				<div class="list-group mb-4">
					<a class="list-group-item list-group-item-info text-center font-weight-bold">Time</a>
					<a href="#" class="list-group-item list-group-item-action text-center font-weight-bold">Data Count</a>
				</div>
			</div>
			<div class="col-lg-9 my-4 mb-4">
				<table class="table">
					<thead>
						<tr>
							<th scope="col">#</th>
							<th scope="col">First</th>
							<th scope="col">Second</th>
							<th scope="col">Third</th>
						</tr>
					</thead>
					<tbody>
					    <tr>
					      <th scope="row">1</th>
					      <td>Mark</td>
					      <td>Otto</td>
					      <td>@mdo</td>
					    </tr>
					    <tr>
					      <th scope="row">2</th>
					      <td>Jacob</td>
					      <td>Thornton</td>
					      <td>@fat</td>
					    </tr>
					    <tr>
					      <th scope="row">3</th>
					      <td colspan="2">Larry the Bird</td>
					      <td>@twitter</td>
					    </tr>
					  </tbody>
				</table>
			</div>
		</div>
	</div>
	<!-- Footer -->
	<!--  
	<footer class="py-5 bg-dark">
		<div class="container">
			<p class="m-0 text-center text-white">Copyright &copy; Codevang 2020</p>
		</div>
	</footer>
	-->
</body>
</html>