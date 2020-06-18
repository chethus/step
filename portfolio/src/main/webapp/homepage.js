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
    const response = await fetch("/comments?" + queryString);

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
 * Create a list entry with the given text.
 */
function createComment(comment) {
    // Set up div for a comment.
    const commentDiv = document.createElement("li");
    commentDiv.setAttribute("class", "comment");

    // Add nickname and comment text.
    commentDiv.innerHTML = `
    <p class="nickname">` + comment.nickname + `</p>
    <p>` + comment.text + `</p>`

    // If the comment has an image, add it.
    if (comment.imageSrc != null) {
        commentDiv.innerHTML += `<img src="` + comment.imageSrc + `">`
    }

    // If the comment has an id (it is the user's own comment), allow the user to edit and delete it.
    if (comment.hasOwnProperty("commentId")) {
        commentDiv.setAttribute("id", "comment-" + comment.commentId);
        commentDiv.innerHTML += `<br/>
        <button onclick=editComment(` + comment.commentId + `)>Edit</button>
        <button onclick=deleteComment(` + comment.commentId + `)>Delete</button>`;
    }

    return commentDiv;
}

/**
 * Sends a comment using a POST request and receives the comment ID.
 */
async function submitComment() {
    // Make request from data in Comment form.
    const commentForm = document.getElementById("comment-form");
    const formData = new FormData(commentForm);

    // Get request URL for the image from blobstore.
    const responseUrl = await fetch("/blobstore-upload-url");
    const urlText = await responseUrl.text();

    const request = new Request(urlText, {method: "POST", body: formData});

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
}

/**
 * Deletes a comment given a comment ID.
 */
async function deleteComment(commentId) {
    const request = new Request("/delete?id=" + commentId, {method: "POST"});
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
 * Edit a comment given a comment ID.
 */
async function editComment(commentId) {
    // Find the appropriate comment.
    const commentDiv = document.getElementById("comment-" + commentId);

    // Get the original text.
    const originalText = commentDiv.children[1].textContent;

    // Create the edit form with a submit button.
    commentDiv.innerHTML = `
    <form enctype="multipart/form-data">
        <label for="text">Text&nbsp;</label>
        <input type="text" name="text" value="` + originalText + `"><br/>
        <label for="image">Image&nbsp; </label>
        <input type="file" name="image">
    </form>
    <button onclick=submitEdit(` + commentId + `)>Submit</button>
    <button onclick=loadComments()>Cancel</button>`
}

/**
 * Submit an edited comment.
 */
async function submitEdit(commentId) {
    // Get the relevant comment.
    const commentDiv = document.getElementById("comment-" + commentId);

    // Get the form data from the edit form and append the comment's ID.
    const editForm = commentDiv.children[0];
    const formData = new FormData(editForm);
    formData.append("commentId", commentId);
    
    // Get the image upload URL from blobstore.
    const responseUrl = await fetch("/blobstore-upload-url");
    const urlText = await responseUrl.text();
    
    const request = new Request(urlText, {method: "POST", body: formData});
    const response = await fetch(request);
    
    // If there is an error, alert the user and exit.
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }

    // Otherwise, reload the comments.
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