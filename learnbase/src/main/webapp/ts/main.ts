console.log("tester");

function userLogin() : void {
  fetch('/userlogin').then(response => response.text()).then((pageContent) => {
    const loginSection = document.getElementById('user-page-content') as HTMLDivElement;
    console.log(pageContent);
    loginSection.innerHTML = pageContent;
  });
}

function getInterests() : void {
  fetch('/interests').then(response => response.json()).then((topics) => {
    const interestsSection = document.getElementById('interests-section') as HTMLDivElement;
    topics.forEach((topic) => {
      console.log(topic);
      interestsSection.innerHTML += topic;
    })
  });
} 

window.onload = function start() : void {
  userLogin();
  getInterests();
}

function pageChanger() : void {
  const navBar = document.getElementById("myTopnav") as HTMLDivElement;
  if (navBar.className === "topnav") { 
    navBar.className += " responsive";
  } else {
    navBar.className = "topnav";
  }
}

