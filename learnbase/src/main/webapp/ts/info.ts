window.onload = function getInfo() : void {
  userLoginStatus();
  fetch('/search').then(response => response.json()).then((response) => {
    console.log(response);
    
    var infoSection = document.getElementById('info-container') as HTMLDivElement;
    response.forEach((element) => {
      console.log(element);
      infoSection.innerHTML += element;
    })
    document.getElementById("loader").style.display = "none";
  });
}
function userLoginStatus() {
  fetch('/status').then(response => response.text()).then((loginStatus) => {
    var status = loginStatus.includes("In");
    if(!status){
      window.location.replace("/index.html");
    }
  });
}