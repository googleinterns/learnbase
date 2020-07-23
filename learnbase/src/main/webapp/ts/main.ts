console.log("tester");

window.onload = function userLogin() {
  fetch('/userlogin').then(response => response.json()).then((pageContent) : void => {
    const loginSection = document.getElementById('user-page-content') as HTMLDivElement;
    loginSection.appendChild(pageContent);
  });
}
