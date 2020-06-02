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
 * Fetches a comment from the server and adds it to the DOM.
 */
let commentText;
$(document).ready(async function getData() {
    const commentJSON = await fetch('/data');
    const comment = await commentJSON.json();
    //Add each element of comment to the container list
    const $list = document.getElementById("comment-container")
    $list.innerHTML = '';
    comment.forEach(text => {$list.appendChild(createLi(text))});
});

/**
 * Creates a list entry with the given text.
 */
function createLi(text) {
    const entry = document.createElement("li");
    entry.textContent = text;
    return entry;
}

/**
 * Adds a random fact to the page.
 */
function randomFact() {
  const facts = [
      'I am a twin.',
      'My initials are the first three letters of the alphabet (CAB).',
      'I learned HTML, CSS, and Javascript to make this website.',
      'I enjoyed math and programming contests in high school.',
      'My astrological sign is Cancer.',
      'I have two brothers and no sisters.',
      'I was born 11 minutes before my twin brother.',
      'I attended 6 different schools from K-12.'
      ];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.textContent = fact;
}