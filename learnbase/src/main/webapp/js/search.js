"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
window.onload = function getTopics() {
    fetch('/topics').then(response => response.json()).then((response) => {
        console.log(response);
        topicManager(response);
        getSimilarTopics(response[response.length - 1]);
    });
};
function topicManager(topics) {
    var table = document.getElementById('subjectTable');
    var i = 0;
    var size = topics.length;
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
    //   for (i = 0; i < topics.length-1; i++){
    //     var newRow = table.insertRow();
    //     var cell = newRow.insertCell(); 
    //     cell.innerHTML = topics[i];
    //     const deleteButtonElement = document.createElement('button');
    //     deleteButtonElement.innerText = 'Delete';
    //     deleteButtonElement.addEventListener('click', () =>{
    //       deleteTopic(topics[i]);
    //     });
    //     var deleteCell = newRow.insertCell();
    //     deleteCell.appendChild(deleteButtonElement);
    //   } 
    //   var time = topics[topics.length-1];
    //   document.getElementById("timeDisplay").innerHTML = time;
}
function deleteTopic(topic) {
    const params = new URLSearchParams();
    params.append("topic", topic);
    fetch('/deleteTopic', { method: 'POST', body: params });
    location.reload();
}
// TODO: Change functionality so that it loads on submit
function getSimilarTopics(topic) {
    return __awaiter(this, void 0, void 0, function* () {
        console.log(`topic: ${topic}`);
        const response = yield fetch(`/recommend-topics?topic=${topic}`);
        const similarTopics = yield response.json();
        console.log(similarTopics);
        return similarTopics;
    });
}
