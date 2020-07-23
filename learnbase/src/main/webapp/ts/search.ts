"use strict";

document.getElementById("searchButton").addEventListener("click", topicManager);
document.getElementById("searchButton").addEventListener("load", getTopics);

function getTopics(){
  fetch('/topics').then(response => response.json()).then((response) =>{
    console.log(response);
    var table = document.getElementById("subjectTable");
    console.log("get topics");
  })  
}

function topicManager() {
    fetch('/topics').then(response => response.text()).then((topics) => {
        console.log(topics);
        console.log("topic manager");
    });
}
