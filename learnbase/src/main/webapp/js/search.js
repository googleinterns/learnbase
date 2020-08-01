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
if (location.pathname === "/search.html") {
    document.getElementById("timeChange").addEventListener("click", timeChangeReveal);
    document.getElementById("submitButton").addEventListener("click", timeChange);
}
window.onload = function getTopics() {
    fetch('/topics').then(response => response.json()).then((response) => {
        console.log(response);
        if (location.pathname === "/search.html") {
            topicManager(response);
        }
        getRecommendedTopics(response).then((result) => {
            if (location.pathname === "/recommendations.html") {
                document.getElementById("loader").style.display = "block";
                document.getElementById("recommended").style.display = "none";
                displayRecommendedTopics(result);
            }
        });
    });
    console.log("First fetch complete");
    if (location.pathname === "/search.html") {
        fetch('/scheduler').then(response => response.text()).then((response) => {
            console.log(response);
            document.getElementById("timeDisplay").innerHTML = response;
        });
        console.log("second fetch complete");
    }
};
function displayRecommendedTopics(recommended) {
    var table = document.getElementById('recommended-topics');
    recommended.forEach((topic) => {
        var newRow = table.insertRow();
        var cell = newRow.insertCell();
        cell.innerHTML = topic.toUpperCase().replace("_", " ");
        document.getElementById("loader").style.display = "none";
        document.getElementById("recommended").style.display = "block";
    });
}
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
                console.log(topic + ": " + results);
                let topicTuple = [topic, similarTopics];
                return topicTuple;
            });
            if (topicInfo[1].length !== 0) {
                topicInfoList.push(topicInfo);
            }
            else {
                continue;
            }
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
        var rangeForFirstTopic = getRandomNumbersNoRepetition(4, 10);
        var rangeForAllOtherTopics = getRandomNumbersNoRepetition(0, 10);
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
                let rand = (currIndex === 0) ? rangeForFirstTopic[i - 4] : rangeForAllOtherTopics[i];
                let nextTopic = topicInfoList[currIndex][1][rand];
                recommendations.push(nextTopic);
            }
        }
        return recommendations;
    });
}
// min inclusive, max exclusive
function getRandomNumbersNoRepetition(min, max) {
    var numbers = [];
    for (let i = min; i < max; i++) {
        numbers.push(i);
    }
    for (let i = numbers.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [numbers[j], numbers[i]] = [numbers[i], numbers[j]];
    }
    return numbers;
}
function topicManager(topics) {
    var table = document.getElementById('subjectTable');
    var i = 0;
    var size = topics.length;
    topics.reverse().forEach((topic) => {
        if (i < 8) {
            var newRow = table.insertRow();
            var cell = newRow.insertCell();
            cell.innerHTML = topic.toUpperCase();
            const deleteButtonElement = document.createElement('button');
            deleteButtonElement.innerText = 'Delete';
            deleteButtonElement.addEventListener('click', () => {
                deleteTopic(topic);
            });
            var deleteCell = newRow.insertCell();
            deleteCell.appendChild(deleteButtonElement);
            i++;
        }
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
        const response = yield fetch(`/recommend-topics?topic=${topic}`);
        const similarTopics = yield response.json();
        return similarTopics;
    });
}
function timeChangeReveal() {
    document.getElementById("selectTime").style.display = "block";
    document.getElementById("currentTime").style.direction = "none";
}
function timeChange() {
    console.log("Button clicked");
    var timeContainer = document.getElementById("appt");
    var time = timeContainer.value;
    var url = "/scheduler?time=" + time;
    console.log(url);
    fetch(url).then(response => response.text()).then((response) => {
        console.log(response);
        document.getElementById("timeDisplay").innerHTML = response;
        document.getElementById("selectTime").style.display = "none";
        document.getElementById("currentTime").style.direction = "block";
    });
}
