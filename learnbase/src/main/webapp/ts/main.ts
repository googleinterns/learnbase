console.log("tester");

window.onload = function userLogin() {
    fetch('/userlogin').then(response => response.json()).then((pageContent) => {
        const loginSection = document.getElementById('user-page-content');
        loginSection.appendChild(pageContent);
    });
}
