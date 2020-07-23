"use strict";
console.log("tester");
window.onload = function userLogin() {
    fetch('/userlogin').then(response => response.text()).then((pageContent) => {
        const loginSection = document.getElementById('user-page-content');
        loginSection.innerHTML = pageContent;
    });
};
function pageChanger() {
    const navBar = document.getElementById("myTopnav");
    if (navBar.className === "topnav") {
        navBar.className += " responsive";
    }
    else {
        navBar.className = "topnav";
    }
}
