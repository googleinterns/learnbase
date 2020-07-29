"use strict";

type TopicInfo = [string, string[]];

document.getElementById("timeChange").addEventListener("click", timeChangeReveal);
document.getElementById("submitButton").addEventListener("click", timeChange);


window.onload = function getTopics() : void {
  fetch('/topics').then(response => response.json()).then((response) =>{
    console.log(response);
    topicManager(response);

    getRecommendedTopics(response);
  });
  
  console.log("First fetch complete");

  fetch('/scheduler').then(response => response.text()).then((response) =>{
    console.log(response);
    document.getElementById("timeDisplay").innerHTML = response; 
  });   
  console.log("second fetch complete");
}

async function getRecommendedTopics(response: string)  {
  var topicInfoList : TopicInfo[] = [];
  var recsPerTopic : number[] = [];
  
  var recommendations : string[] = [];

  for (let i = response.length-1; i >= 0; i--) {
    let topic : string = response[i];
    let similarTopics : string[];

    let topicInfo : TopicInfo = await getSimilarTopics(topic).then((results : string[]) => {
      similarTopics = results;
      let topicTuple : TopicInfo = [topic, similarTopics];

      return topicTuple;
    });

    topicInfoList.push(topicInfo);
    recsPerTopic.push(0);  
  }

  // First topic has 4 automatic recommendations from most recent choice.
  // Other 6 are drawn from random distribution of all topics.
  for (let i = 0; i < 6; i++) {
    let rand : number = Math.random()*10000;
    let j = 1;
    
    if (rand < 10000/Math.pow(2, topicInfoList.length-1)) {
      recsPerTopic[0] += 1;
    }
    while (j < topicInfoList.length) {
      if (rand > 10000/Math.pow(2, j)) {
        recsPerTopic[j] += 1;
        break;
      }
      j++;
    }

    console.log(rand);

  }
  
  var currIndex : number = 0;
  for (let i = 0; i < 10; i++) {
    if (i < 4) {
      recommendations.push(topicInfoList[0][1][i]);
    } else {
      
      if (recsPerTopic[currIndex] === 0) {
        currIndex += 1;
      } else {
        recsPerTopic[currIndex] -= 1
      }
      
      // TODO: Eliminate duplicates
      let rand : number = (currIndex === 0) ? Math.floor(Math.random()* 6) + 4 : Math.floor(Math.random() * 10);
      let nextTopic : string = topicInfoList[currIndex][1][rand];
      
      recommendations.push(nextTopic);
    }
  }

  console.log("Recommendations: " + recommendations);
}

function getRandomNumbersNoRepetition(min: number, max: number) : number[] {
  var numbers : number[];
  
  return numbers;
}

function topicManager(topics: string[]) : void {
  var table = document.getElementById('subjectTable') as HTMLTableElement;
  var i = 0 ; 
  var size = topics.length;
  topics.forEach((topic: string) => {
    var newRow = table.insertRow();
    var cell = newRow.insertCell(); 
    cell.innerHTML = topic;

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () =>{
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

function deleteTopic(topic: string) : void {
  const params = new URLSearchParams(); 
  params.append("topic", topic)
  fetch('/deleteTopic', {method: 'POST', body: params});
  location.reload();
}

// TODO: Change functionality so that it loads on submit
async function getSimilarTopics(topic: string) : Promise<string[]> {
  console.log(`topic: ${topic}`);

  const response = await fetch(`/recommend-topics?topic=${topic}`);
  const similarTopics = await response.json();

  return similarTopics;
}

function timeChangeReveal() {
  document.getElementById("selectTime").style.display = "block"; 
  document.getElementById("currentTime").style.direction = "none";
}

function timeChange(){
  console.log("Button clicked");
  var timeContainer = document.getElementById("appt") as HTMLInputElement;
  var time = timeContainer.value; 
  var url = "/scheduler?time=" + time; 
  console.log(url);
  fetch(url).then(response => response.text()).then((response) =>{
    console.log(response);
    document.getElementById("timeDisplay").innerHTML = response; 
    document.getElementById("selectTime").style.display = "none"; 
    document.getElementById("currentTime").style.direction = "block";
  });

}


