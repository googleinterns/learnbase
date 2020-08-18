window.onload = function getInfo() : void {
  userStatus();
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

