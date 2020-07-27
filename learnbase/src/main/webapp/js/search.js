"use strict";
document.getElementById("timeChange").addEventListener("click", timeChange);
//document.getElementById("body").addEventListener("load", getTopics);
function timeChange() {
    document.getElementById("selectTime").style.display = "block";
    document.getElementById("currentTime").style.direction = "none";
}
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
    location.reload();
}
