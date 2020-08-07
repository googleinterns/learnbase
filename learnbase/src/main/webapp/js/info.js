"use strict";
window.onload = function getInfo() {
    fetch('/search').then(response => response.json()).then((response) => {
        console.log(response);
        var infoSection = document.getElementById('info-container');
        response.forEach((element) => {
            console.log(element);
            infoSection.innerHTML += element;
        });
    });
};
