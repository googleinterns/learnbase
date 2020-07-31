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
        getRecommendedTopics(response).then((result) => {
            displayRecommendedTopics(result);
        });
    });
    console.log("First fetch complete");
    fetch('/scheduler').then(response => response.text()).then((response) => {
        console.log(response);
        document.getElementById("timeDisplay").innerHTML = response;
    });
    console.log("second fetch complete");
};
function displayRecommendedTopics(recommended) {
    var table = document.getElementById('recommended-topics');
    recommended.forEach((topic) => {
        var newRow = table.insertRow();
        var cell = newRow.insertCell();
        cell.innerHTML = topic;
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
