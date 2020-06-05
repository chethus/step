/**
 * Fetches comments from the server and adds them to the DOM.
 */
async function loadComments() {

    // Get comment limit from form.
    const selectMax = document.getElementById("select-max");
    const maxComments = selectMax.options[selectMax.selectedIndex].value;
  
    // Request JSON based on user comment limit.
    const commentsJSON = await fetch("/data?max=" + maxComments);
    // Convert JSON to object.
    const comments = await commentsJSON.json();

    // Add all comments to comment container.
    const container = document.getElementById("comment-container");
    container.innerHTML = "";
    comments.forEach(comment => container.innerHTML += createComment(comment).outerHTML);
}

$(document).ready(loadComments);

/**
 * Create a list entry with the given text.
 */
function createComment(comment) {
    
    // Set up div for a comment.
    const commentDiv = document.createElement("div");
    commentDiv.innerHTML = "";

    // Add paragraphs for author, subject, and comment text.
    commentDiv.appendChild(createP("Author: " + comment.author));
    commentDiv.appendChild(createP("Subject: " + comment.subject));
    commentDiv.appendChild(createP(comment.text));

    return commentDiv;
}

/**
 * Create a paragraph with the given text.
 */
function createP(text) {
    const p = document.createElement("p");
    p.textContent = text;
    return p;
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