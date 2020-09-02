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
// Get the topics and recommendations, and display
// them on the webpage.
window.onload = function getTopics() {
    var loggedIn = userStatus();
    var showSelectedTopicsBox;
    var selectedTopics = new Set();
    fetch('/topics').then(response => response.json()).then((response) => {
        if (JSON.stringify(response) !== "{}") {
            response.forEach((topic) => {
                selectedTopics.add(topic.toLowerCase().replace(" ", "_"));
            });
        }
        showSelectedTopics(showSelectedTopicsBox, response);
        getRecommendedTopics(response).then((result) => {
            loaderDisplay(result, selectedTopics);
        });
    });
    if (location.pathname === "/search.html") {
        fetch('/scheduler').then(response => response.text()).then((response) => {
            document.getElementById("timeDisplay").innerHTML = response;
        });
    }
};
function showSelectedTopics(showSelectedTopicsBox, response) {
    if (location.pathname === "/search.html" || location.pathname === "/recommendations.html") {
        showSelectedTopicsBox = topicManager(response);
    }
    if (showSelectedTopicsBox === 1) {
        document.getElementById("subjectTableContainer").style.display = "block";
    }
}
function loaderDisplay(result, selectedTopics) {
    if (location.pathname === "/recommendations.html") {
        if (result.length === 0) {
            document.getElementById("no-recs").style.display = "block";
            document.getElementById("loader").style.display = "none";
            document.getElementById("recommended-topics").style.display = "none";
        }
        else {
            document.getElementById("loader").style.display = "block";
            document.getElementById("recommended-topics").style.display = "none";
            displayRecommendedTopics(result, selectedTopics);
        }
    }
}
// Display recommended topics
function displayRecommendedTopics(recommended, selectedTopics) {
    var table = document.getElementById('recommended-topics');
    var setOfTopics = new Set();
    recommended.forEach((topic) => {
        if (!setOfTopics.has(topic) && !selectedTopics.has(topic)) {
            setOfTopics.add(topic);
            var cell = createRecTopicCell(table, topic);
            cell.style.fontSize = "18px";
            cell.innerHTML = capital_letter(topic.toLowerCase().replace("_", " "));
            document.getElementById("loader").style.display = "none";
            document.getElementById("recommended-topics").style.display = "table";
        }
    });
}
function createRecTopicCell(table, topic) {
    var newRow = table.insertRow();
    var cell = newRow.insertCell();
    cell.setAttribute('class', 'rec');
    cell.addEventListener('mouseover', () => {
        cell.style.color = "#009900";
    });
    cell.addEventListener('mouseout', () => {
        cell.style.color = "#656565";
    });
    cell.addEventListener('click', () => {
        topic = topic.replace("_", " ");
        const params = new URLSearchParams();
        params.append("topic", topic);
        document.getElementById("loader").style.display = "block";
        document.getElementById("recommended-topics").style.display = "none";
        fetch('/topics', { method: 'POST', body: params }).then(() => {
            createSelectedTopic(topic, document.getElementById('subjectTable'));
            location.reload();
        });
    });
    return cell;
}
/*
 * Get top 10 recommended topics. The first 3 are
 * the most similar topics to the most recently queried
 * subject, and the remaining 7 are drawned from a random
 * distribution.
 */
function getRecommendedTopics(response) {
    return __awaiter(this, void 0, void 0, function* () {
        var topicInfoList = [];
        var doRecsExist = false;
        if (JSON.stringify(response) === "{}")
            return [];
        // For each topic the user has selected, get all ofthe similar topics. Also initialize recsPerTopic list.
        for (let i = response.length - 1; i >= 0; i--) {
            let topic = response[i];
            let similarTopics;
            let topicInfo = yield getSimilarTopics(topic).then((results) => {
                similarTopics = results;
                let topicTuple = [topic, similarTopics];
                return topicTuple;
            });
            if (topicInfo[1].length !== 0) {
                topicInfoList.push(topicInfo);
                doRecsExist = true;
            }
            else {
                continue;
            }
        }
        if (!doRecsExist)
            return [];
        var recsPerTopic = pickTenRandomTopics(topicInfoList, response);
        return createRecsList(topicInfoList, recsPerTopic);
    });
}
// First topic has 3 automatic recommendations from most recent choice.
// Other 7 are drawn from random distribution of all topics.
// Counts are stored in the recsPerTopic list.
function pickTenRandomTopics(topicInfoList, response) {
    // Keeps track of number of recommendations for each topic that will be displayed.
    var recsPerTopic = Array(response.length).fill(0);
    for (let i = 0; i < 7; i++) {
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
    }
    return recsPerTopic;
}
function createRecsList(topicInfoList, recsPerTopic) {
    var rangeForFirstTopic = getRandomNumbersNoRepetition(3, 10);
    var rangeForAllOtherTopics = getRandomNumbersNoRepetition(0, 10);
    var recommendations = [];
    var currIndex = 0;
    for (let i = 0; i < 10; i++) {
        if (i < 3) {
            recommendations.push(topicInfoList[0][1][i]);
        }
        else {
            if (recsPerTopic[currIndex] === 0) {
                currIndex += 1;
            }
            else {
                recsPerTopic[currIndex] -= 1;
            }
            let rand = (currIndex === 0) ? rangeForFirstTopic[i - 3] : rangeForAllOtherTopics[i];
            let nextTopic = topicInfoList[currIndex][1][rand];
            recommendations.push(nextTopic);
        }
    }
    return recommendations;
}
// Gets list of indices in a random order, and pulls
// each index based off of the results of the 
// random distribution above.
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
// Displays list of selected topics to the screen.
// Will only display the 8 most recently searched topics.
function topicManager(topics) {
    var table = document.getElementById('subjectTable');
    var i = 0;
    var size = topics.length;
    if (size === undefined) {
        return 0;
    }
    topics.forEach((topic) => {
        createSelectedTopic(topic, table);
        i++;
    });
    return 1;
}
// Creates cell for a topic to display under "Selected Topics".
function createSelectedTopic(topic, table) {
    var newRow = table.insertRow(0);
    var cell = newRow.insertCell();
    cell.innerHTML = capital_letter(topic);
    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
        deleteTopic(topic.toLowerCase());
    });
    var deleteCell = newRow.insertCell();
    deleteCell.appendChild(deleteButtonElement);
}
// Capitalizes first letter of each word in str.
function capital_letter(str) {
    str = str.split(" ");
    for (var i = 0, x = str.length; i < x; i++) {
        str[i] = str[i][0].toUpperCase() + str[i].substr(1);
    }
    return str.join(" ");
}
// Deletes topic.
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
    var timeContainer = document.getElementById("appt");
    var emailOption = document.getElementById("yn");
    var time = timeContainer.value;
    var choice = emailOption.options[emailOption.selectedIndex].value;
    var url = "/scheduler?time=" + time + "&optIn=" + choice;
    fetch(url).then(response => response.text()).then((response) => {
        document.getElementById("timeDisplay").innerHTML = response;
        document.getElementById("selectTime").style.display = "none";
        document.getElementById("currentTime").style.direction = "block";
    });
}
function userStatus() {
    fetch('/status').then(response => response.text()).then((loginStatus) => {
        var status = loginStatus.includes("In");
        if (!status) {
            window.location.replace("/index.html");
        }
    });
}
