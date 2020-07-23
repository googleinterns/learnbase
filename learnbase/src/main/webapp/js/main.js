"use strict";
console.log("tester");
document.getElementById("searchButton").addEventListener("click", topicManager);
window.onload = function userLogin() {
    fetch('/userlogin').then(response => response.text()).then((pageContent) => {
        const loginSection = document.getElementById('user-page-content');
        loginSection.innerHTML = pageContent;
    });
};
<<<<<<< HEAD
function topicManager() {
    fetch('/topics').then(response => response.text()).then((topics) => {
        console.log(topics);
    });
}
function pageChanger() {
    const navBar = document.getElementById("myTopnav");
    if (navBar.className === "topnav") {
        navBar.classname += " responsive";
    }
    else {
        x.className = "topnav";
=======
function pageChanger() {
    const navBar = document.getElementById("myTopnav");
    if (navBar.className === "topnav") {
        navBar.className += " responsive";
    }
    else {
        navBar.className = "topnav";
>>>>>>> 36fa1ca4371738dac7d953e9293730c4af7264e0
    }
}
