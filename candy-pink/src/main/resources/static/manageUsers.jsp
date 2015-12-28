<%@ page import="urlshortener2015.candypink.repository.UserRepositoryImpl" %>
<!DOCTYPE html>
<html>
<head>
<title>URL Shortener</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
<link type="text/css" rel="stylesheet" href="css/bgStyle.css" media="all" />
<link type="text/css" rel="stylesheet" href="css/form.css" media="all" />
<script type="text/javascript" src="webjars/jquery/2.1.4/jquery.min.js"></script>
<script type="text/javascript" src="webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript" src="js/manageUsers.js"></script>
</head>
<body onload="showUsers(<% (new UserRepositoryImpl()).getAllUsers() %>)">
	<!-- Imagen de cabecera del sitio web -->
	<center><img src="images/CandyLogoTrans.png" height="103" alt="CandyShort logo"/></center>
	<!-- Menú con los botones descritos a continuación -->
	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div>
				<ul class="nav navbar-nav">
					<!-- Home -->
					<li><a href="index.html"><span class="glyphicon glyphicon-home"></span> Home</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<!-- Enlace a registro -->
					<li><a href="signUpPage.html"><span class="glyphicon glyphicon-user"></span> Sign Up</a></li>
					<!-- Enlace a identificarse -->
					<li><a href="loginPage.html"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
				</ul>
			</div>
		</div>
	</nav>
  	<div class="container">
  		<!-- Cabecera -->
		<header>
			<h1><span>Admin</span> Manage your users </h1>
        	</header>
		<div class="row">
			<div class="col-lg-12 text-center">
				<!-- Usuarios -->
				<div class="col-sm-offset-4 col-sm-4 text-center">
					<br />
					<div id="users"></div>
				</div><br /> <br />
				<!-- Boton para añadir un nuevo usuario -->
				<div class="col-sm-offset-4 col-sm-4 text-center">
					<span class="input-group-btn"><button class="btn btn-lg btn-primary">
						<a href="signUpPage.html"><span class="glyphicon glyphicon-user"></span> Add a new user!</a>
					</button></span>
				</div>
			</div>
		</div>
	</div>
</body>
</html>