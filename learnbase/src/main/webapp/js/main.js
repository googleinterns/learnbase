"use strict";
console.log("tester");
window.onload = function userLogin() {
    fetch('/userlogin').then(response => response.text()).then((pageContent) => {
        const loginSection = document.getElementById('user-page-content');
        console.log(pageContent);
        loginSection.innerHTML = pageContent;
    });
};
function getInterests() {
    fetch('/interests').then(response => response.json()).then((topics) => {
        const interestsSection = document.getElementById('interests-section');
        topics.forEach((topic) => {
            console.log(topic);
            interestsSection.innerHTML += topic;
        });
    });
}
//window.onload = function start() : void {
//  userLogin();
//  getInterests();
//}
function pageChanger() {
    const navBar = document.getElementById("myTopnav");
    if (navBar.className === "topnav") {
        navBar.className += " responsive";
    }
    else {
        navBar.className = "topnav";
    }
}
