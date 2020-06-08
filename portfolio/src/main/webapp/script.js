// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() 
{
  const greetings =
      ['Cheeseburgers are my favorite food', 'I don\'t like peanut butter', 
       'Blue is my favorite color', 'Heights and cockroaches terrify me',
       'I was a swimmer in highschool', 'Bojack Horseman is my favorite show on Netflix',
       'I\'m learning how to kickbox'
      ];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function loadComments() 
{
  fetch('/data').then(response => response.json()).then((tasks) => 
  {
    const taskListElement = document.getElementById('task-list');

    tasks.forEach((task) => 
    {
      taskListElement.appendChild(createTaskElement(task));
    })
    
  });
}

/** Creates an element that represents a task, including its delete button. */
function createTaskElement(task) 
{
    const taskElement = document.createElement('li');
    taskElement.className = 'task';

    const titleElement = document.createElement('span');
    titleElement.innerText = task.comment;

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    
    deleteButtonElement.addEventListener('click', () => 
    {
        deleteTask(task);

        // Remove the task from the DOM.
        taskElement.remove();
    });

    taskElement.appendChild(titleElement);
    taskElement.appendChild(deleteButtonElement);
    return taskElement;
}

/** Tells the server to delete the task. */
function deleteTask(task) {
  const params = new URLSearchParams();
  params.append('id', task.id);
  fetch('/delete-task', {method: 'POST', body: params});
}
