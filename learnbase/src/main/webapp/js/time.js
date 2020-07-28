"use strict";
document.getElementById("timeChange").addEventListener("click", timeChangeReveal);
document.getElementById("submitButton").addEventListener("click", timeChange);
window.onload = function getTime() {
    fetch('/scheduler').then(response => response.json()).then((response) => {
        console.log(response);
        document.getElementById("timeDisplay").innerHTML = response;
    });
};
function timeChangeReveal() {
    document.getElementById("selectTime").style.display = "block";
    document.getElementById("currentTime").style.direction = "none";
}
function timeChange() {
    console.log("Button clicked");
    var timeContainer = document.getElementById("appt");
    var time = timeContainer.value;
    var url = "/scheduler?time=" + time;
    console.log(url);
    fetch(url).then(response => response.text()).then((response) => {
        console.log(response);
        document.getElementById("timeDisplay").innerHTML = response;
        document.getElementById("selectTime").style.display = "none";
        document.getElementById("currentTime").style.direction = "block";
    });
}
