console.log("tester");

window.onload = function userLogin() : void {
  fetch('/userlogin').then(response => response.text()).then((pageContent) => {
    const loginSection = document.getElementById('user-page-content') as HTMLDivElement;
    try{
      loginSection.innerHTML = pageContent;
    } catch (error){
      console.log(error); 
    }
  });
} 


function pageChanger() : void {
  const navBar = document.getElementById("myTopnav") as HTMLDivElement;
  if (navBar.className === "topnav") { 
    navBar.className += " responsive";
  } else {
    navBar.className = "topnav";
  }
}

