console.log("tester");

window.onload = function userLogin() : void {
  fetch('/userlogin').then(response => response.text()).then((pageContent) => {
    const loginSection = document.getElementById('user-page-content') as HTMLDivElement;
    loginSection.innerHTML = pageContent;
  });
} 

fucntion pageChanger() {
  const navBar = document.getElementById("myTopnav");
  if (navBar.className === "topnav") { 
    navBar.classname += " responsive";
  } else {
    navBar.className = "topnav";
  }
}
