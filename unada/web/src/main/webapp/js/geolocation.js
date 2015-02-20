var latitude;
var longitude;
function getLocation() {
	if (document.readyState === "complete") {
		latitude = document.getElementById("configuration:latitude");
		longitude = document.getElementById('configuration:longitude');
	}
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(showPosition);
	}
}

function showPosition(position) {

	latitude.value = position.coords.latitude;
	longitude.value = position.coords.longitude;
}
