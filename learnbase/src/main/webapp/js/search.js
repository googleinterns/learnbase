"use strict";
//document.getElementById("searchButton").addEventListener("click", topicManager);
//document.getElementById("body").addEventListener("load", getTopics);
window.onload = function getTopics() {
    fetch('/topics').then(response => response.json()).then((response) => {
        console.log(response);
        topicManager(response);
    });
};
function topicManager(topics) {
    var table = document.getElementById('subjectTable');
    topics.array.forEach(topic => {
        var newRow = table.insertRow();
        var cell = newRow.insertCell();
        cell.innerHTML = topic;
    });
}
