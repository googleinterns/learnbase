window.onload = function userLogin() : void {
  fetch('/status').then(response => response.text()).then((loginStatus) => {
    var status = loginStatus.includes("In");
    if(!status){
      window.location.replace("/index.html");
    }
  });
}