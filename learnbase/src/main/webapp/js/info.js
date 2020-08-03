"use strict";
window.onload = function getInfo() {
    fetch('/search').then(response => response.json()).then((response) => {
        console.log(response);
        var infoSection = document.getElementById('info-container');
        infoSection.innerHTML = response;
    });
};
