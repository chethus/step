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
    const commentDiv = document.createElement("li");
    commentDiv.setAttribute("class", "comment");
    commentDiv.innerHTML = "";

    // Add paragraphs for author and comment text.
    const author = createP(comment.author);
    author.setAttribute("class", "name");
    commentDiv.appendChild(author);
    commentDiv.appendChild(createP(comment.text));

    return commentDiv;
}

/**
 * Sends a Comment using a POST request and receives the Comment ID.
 */
async function submitComment() {

    // Make request from data in Comment form.
    const commentForm = document.getElementById("comment-form");
    const formData = (new URLSearchParams(new FormData(commentForm))).toString();
    const request = new Request("/data?" + formData, {method: "POST"});

    // Reset Comment form and get response from request.
    commentForm.reset();
    const response = await fetch(request);

    // Update comments with new comment.
    loadComments();

    // Provide user with their comment ID.
    const responseText = await response.text();
    let message = "Your comment ID is below. ";
    message += "Copy this to delete your comment later.";
    prompt(message, responseText);
}

/**
 * Delete all comments and update the page.
 */
async function deleteAll() {

    // Send a request to DeleteServlet and reload comments.
    const request = new Request("/delete", {method: "POST"});
    const response = await fetch(request);
    loadComments();
}

/**
 * Deletes a Comment using the Comment ID in the delete form.
 */
async function deleteComment() {

    // Extract delete form data into request.
    const deleteForm = document.getElementById("delete-form");
    const formData = (new URLSearchParams(new FormData(deleteForm))).toString();
    const request = new Request("/delete?" + formData, {method: "POST"});
    
    deleteForm.reset();

    // Check response status and update comments.
    const response = await fetch(request);
    if (response.status === 500) {
        alert("Comment ID not found");
    }
    loadComments();
}

/**
 * Create a paragraph with the given text.
 */
function createP(text) {
    const p = document.createElement("p");
    p.textContent = text;
    return p;
}