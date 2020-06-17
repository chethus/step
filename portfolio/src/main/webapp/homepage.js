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

    // Check if page field is valid.
    let page = $("#comment-page").val();
    if (parseInt(page) != page || parseInt(page) <= 0) {
        alert("Invalid page");
        $("#comment-page").val(1);
        page = 1;
    }

    // Get comment limit from form.
    const selectMax = document.getElementById("comment-max");
    const maxComments = selectMax.options[selectMax.selectedIndex].value;

    let queryString = "max=" + maxComments;
    queryString += "&page=" + page;
  
    // Request JSON based on user comment limit.
    const response = await fetch("/data?" + queryString);

    // If there is an error, alert with the message.
    if (response.status >= 400) {
        $("#comment-page").val(1);
        page = 1;
        alert(await response.text());
    }

    // Otherwise, convert JSON to object.
    const comments = await response.json();

    // Add all comments to comment container.
    const container = document.getElementById("comment-container");
    container.innerHTML = "";
    comments.forEach(comment => container.innerHTML += createComment(comment).outerHTML);
}

$(document).ready(loadComments);

/**
 * Load comment form based on login status.
 */
async function loadCommentForm() {

    // Request user information from server.
    const responseJSON = await fetch("/user");
    const response = await responseJSON.json();
    
    // If the user is not logged in, the server responds with a login url.
    if (typeof response === "string") {

        // Unhide a login button with the login url.
        $("login").css("display", "inline-block");
        loginBtn.setAttribute("onclick","location.href=\"" + response + "\"");
    } else {

        // Otherwise, unhide the comment form.
        $("#comment-form").css("display", "block");
        $("#comment-submit").css("display", "inline-block");
    }
}

$(document).ready(loadCommentForm);

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

    // Reset comment form and get response from request.
    commentForm.reset();
    const response = await fetch(request);

    // If there was an error, alert with response text and exit.
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }

    // Otherwise, update comments with new comment.
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

    // If there was an error, alert and exit.
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }

    // Otherwise, reload the comments.
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
    if (response.status >= 400) {
        alert(await response.text());
        return;
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

/**
 * Moves to next page of comments.
 */
function nextPage() {
    $("#comment-page").val(parseInt($("#comment-page").val()) + 1);
    loadComments();
}

/**
 * Moves to previous page of comments.
 */
function prevPage() {
    $("#comment-page").val(parseInt($("#comment-page").val()) - 1);
    loadComments();
}