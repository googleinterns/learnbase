"use strict";

document.getElementById("timeChange").addEventListener("click", timeChangeReveal);
document.getElementById("selectTime").addEventListener("click", timeChange);

function timeChangeReveal(){
  document.getElementById("selectTime").style.display = "block"; 
  document.getElementById("currentTime").style.direction = "none";
}

function timeChange(){
  var timeContainer = document.getElementById("appt") as HTMLInputElement;
  var time = timeContainer.value; 
  var url = "/scheduler?time=" + time; 
  fetch(url).then(response => response.text()).then((response) =>{
    console.log(response);
  });
}


