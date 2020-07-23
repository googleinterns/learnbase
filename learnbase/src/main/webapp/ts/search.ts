"use strict";
console.log("tester");
document.getElementById("searchButton").addEventListener("click", topicManager);
document.getElementById("searchButton").addEventListener("load", getTopics);

function getTopics(){
  fetch('/topics').then(response => response.json()).then((response) =>{
    console.log(response);
    var table = document.getElementById("subjectTable");
    
  })  
}

function topicManager() {
    fetch('/topics').then(response => response.text()).then((topics) => {
        console.log(topics);
    });
}
