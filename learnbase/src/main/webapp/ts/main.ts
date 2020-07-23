console.log("tester");

document.getElementById("searchButton").addEventListener("click", topicManager)



window.onload = function userLogin() : void {
  fetch('/userlogin').then(response => response.text()).then((pageContent) => {
    const loginSection = document.getElementById('user-page-content') as HTMLDivElement;
    loginSection.innerHTML = pageContent;
  });
} 

function topicManager(){
  fetch('/topics').then(response => response.text()).then((topics) => {
    console.log(topics)
  });
}

function pageChanger() {
  const navBar = document.getElementById("myTopnav") as HTMLDivElement;
  if (navBar.className === "topnav") { 
    navBar.className += " responsive";
  } else {
    navBar.className = "topnav";
  }
}
