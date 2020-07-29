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
        getRecommendedTopics(response);
    });
};
function getRecommendedTopics(response) {
    return __awaiter(this, void 0, void 0, function* () {
        var topicInfoList = [];
        var recsPerTopic = [];
        var recommendations = [];
        for (let i = response.length - 1; i >= 0; i--) {
            let topic = response[i];
            let similarTopics;
            let topicInfo = yield getSimilarTopics(topic).then((results) => {
                similarTopics = results;
                let topicTuple = [topic, similarTopics];
                return topicTuple;
            });
            topicInfoList.push(topicInfo);
            recsPerTopic.push(0);
        }
        // First topic has 4 automatic recommendations from most recent choice.
        // Other 6 are drawn from random distribution of all topics.
        for (let i = 0; i < 6; i++) {
            let rand = Math.random() * 10000;
            let j = 1;
            if (rand < 10000 / Math.pow(2, topicInfoList.length - 1)) {
                recsPerTopic[0] += 1;
            }
            while (j < topicInfoList.length) {
                if (rand > 10000 / Math.pow(2, j)) {
                    recsPerTopic[j] += 1;
                    break;
                }
                j++;
            }
            console.log(rand);
        }
        var currIndex = 0;
        for (let i = 0; i < 10; i++) {
            if (i < 4) {
                recommendations.push(topicInfoList[0][1][i]);
            }
            else {
                if (recsPerTopic[currIndex] === 0) {
                    currIndex += 1;
                }
                else {
                    recsPerTopic[currIndex] -= 1;
                }
                // TODO: Eliminate duplicates
                let rand = (currIndex === 0) ? Math.floor(Math.random() * 6) + 4 : Math.floor(Math.random() * 10);
                let nextTopic = topicInfoList[currIndex][1][rand];
                recommendations.push(nextTopic);
            }
        }
        console.log("Recommendations: " + recommendations);
    });
}
function getRandomNumbersNoRepetition(min, max) {
    var numbers;
    return numbers;
}
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
// TODO: Change functionality so that it loads on submit
function getSimilarTopics(topic) {
    return __awaiter(this, void 0, void 0, function* () {
        console.log(`topic: ${topic}`);
        const response = yield fetch(`/recommend-topics?topic=${topic}`);
        const similarTopics = yield response.json();
        return similarTopics;
    });
}
