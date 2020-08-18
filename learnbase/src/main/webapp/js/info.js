"use strict";
window.onload = function getInfo() {
    userStatus();
    fetch('/search').then(response => response.json()).then((response) => {
        console.log(response);
        var infoSection = document.getElementById('info-container');
        response.forEach((element) => {
            console.log(element);
            infoSection.innerHTML += element;
        });
        document.getElementById("loader").style.display = "none";
    });
};
