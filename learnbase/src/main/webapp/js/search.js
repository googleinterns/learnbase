"use strict";
//document.getElementById("searchButton").addEventListener("click", topicManager);
//document.getElementById("body").addEventListener("load", getTopics);
window.onload = function getTopics() {
    fetch('/topics').then(response => response.json()).then((response) => {
        console.log(response);
        var topics = JSON.parse(response);
        console.log(topics);
        var table = document.getElementById("subjectTable");
        console.log("get topics");
    });
};
