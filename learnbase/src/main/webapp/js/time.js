"use strict";
document.getElementById("timeChange").addEventListener("click", timeChangeReveal);
document.getElementById("submitButton").addEventListener("click", timeChange);
window.onload = function getTime() {
    fetch('/scheduler').then(response => response.json()).then((response) => {
        console.log(response);
        document.getElementById("timeDisplay").innerHTML = response;
    });
};
