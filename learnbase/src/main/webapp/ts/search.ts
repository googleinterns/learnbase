"use strict";

//document.getElementById("searchButton").addEventListener("click", topicManager);
//document.getElementById("body").addEventListener("load", getTopics);

window.onload = function getTopics() : void {
  fetch('/topics').then(response => response.json()).then((response) =>{
    console.log(response);
    topicManager(response);
  })  
}

window.onchange = async function getSimilarTopics() : Promise<void> {
  const topic : string = (document.getElementById('topic') as HTMLInputElement).value;
  console.log(`topic: ${topic}`);

  const response = await fetch(`/recommend-topics?topic=${topic}`);
  const similarTopics = await response.json();

  console.log(similarTopics);
  return similarTopics;
}

function topicManager(topics) : void {
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

function deleteTopic(topic) : void {
  const params = new URLSearchParams(); 
  params.append("topic", topic)
  fetch('/deleteTopic', {method: 'POST', body: params});
  location.reload();
}

