"use strict";

document.getElementById("timeChange").addEventListener("click", timeChange);
//document.getElementById("body").addEventListener("load", getTopics);

function timeChange(){
  document.getElementById("selectTime").style.display = "block"; 
  document.getElementById("currentTime").style.direction = "none";
}


window.onload = function getTopics() : void {
  fetch('/topics').then(response => response.json()).then((response) =>{
    console.log(response);
    topicManager(response);
    getSimilarTopics(response[response.length - 1]);
  })  
}

function topicManager(topics: string[]) : void {
  var table = document.getElementById('subjectTable') as HTMLTableElement;
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

}

function deleteTopic(topic: string) : void {
  const params = new URLSearchParams(); 
  params.append("topic", topic)
  fetch('/deleteTopic', {method: 'POST', body: params});
  location.reload();
}

// TODO: Change functionality so that it loads on submit
async function getSimilarTopics(topic: string) : Promise<void> {
  console.log(`topic: ${topic}`);

  const response = await fetch(`/recommend-topics?topic=${topic}`);
  const similarTopics = await response.json();

  console.log(similarTopics);
  return similarTopics;
}

