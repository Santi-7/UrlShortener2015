function showSafeOptions() {
	if (document.getElementById("safe").innerHTML == " I want a safe short Url") {
		document.getElementById("safe").innerHTML = " I don't want a safe short Url";
		$("p").show();
	} else {
		document.getElementById("safe").innerHTML = " I want a safe short Url";
		$('#users').val('select');
		$('#time').val('select');
		$("p").hide();
	}
}
