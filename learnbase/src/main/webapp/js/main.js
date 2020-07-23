"use strict";
console.log("tester");
window.onload = function userLogin() {
    fetch('/userlogin').then(response => response.text()).then((pageContent) => {
        const loginSection = document.getElementById('user-page-content');
        loginSection.innerHTML = pageContent;
    });
};
