$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>"
                            + "<div class='alert alert-success lead'>And your token is: "
                            + msg.token
                            + "</div>");
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>An error has ocurred</div>"
                                + "<div class=\"col-sm-offset-4 col-sm-4 text-center\">"
                                	/* Error in JSON */
                                	+ "<a href=\"signUpPage.html\">"
						+ "<span class=\"input-group-btn\"><button class=\"btn btn-lg btn-primary\">"
							+ JSON
						+ "</button></span>"
					+ "</a>"
					/* Error in HTML */
					+ "<a href=\"signUpPage.html\">"
						+ "<span class=\"input-group-btn\"><button class=\"btn btn-lg btn-primary\">"
							+ HTML
						+ "</button></span>"
					+ "</a>"
				+ "</div>"
			);
                    }
                });
            });
    });
