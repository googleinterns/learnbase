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
    topics.forEach((topic) => {
        var newRow = table.insertRow();
        var cell = newRow.insertCell();
        cell.innerHTML = topic;
        const deleteButtonElement = document.createElement('button');
        deleteButtonElement.innerText = 'Delete';
        deleteButtonElement.addEventListener('click', () => {
            deleteTopic(topic);
        });
        var deleteCell = newRow.insertCell();
        deleteCell.appendChild(deleteButtonElement);
    });
}
function deleteTopic(topic) {
    const params = new URLSearchParams();
    params.append("topic", topic);
    fetch('/deleteTopic', { method: 'POST', body: params });
}
