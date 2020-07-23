console.log("tester");

window.onload = function userLogin() : void {
  fetch('/userlogin').then(response => response.text()).then((pageContent) => {
    const loginSection = document.getElementById('user-page-content') as HTMLDivElement;
    loginSection.innerHTML = pageContent;
  });
}
