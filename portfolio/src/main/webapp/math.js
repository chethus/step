let startBtn;
let instructions;
let question;
let scores;
let score;
let startTime;

// Setting variables for some elements to get convenient access.
window.onload = function setUpGame() {
    startBtn = document.getElementById("start");
    instructions = document.getElementById("instructions");
    question = document.getElementById("question");
    answer = document.getElementById("answer");
    submit = document.getElementById("submit");
    scores = document.getElementById("scores");
}

// Remove intro screen and show questions when game starts.
async function startGame() {
    instructions.style.display = "none";
    startBtn.style.display = "none";
    question.style.display = "inline-block";
    answer.style.display = "inline";
    submit.style.display = "inline-block";
    scores.style.display = "inline";
    score = 0;
    startTime = new Date().getTime();
    const getRequest = new Request("game?start=" + startTime, {method: "GET"});
    const getResponse = await fetch(getRequest);
    const getResponseText = await getResponse.text();

    // If there was a server error, alert and exit.
    if (getResponse.status >= 400) {
        alert(getResponseText);
        return;
    }

    question.src = "data:image/png;base64, " + getResponseText;
    setTimeout(gameOver, 60000);
}

async function check() {
    const answer = document.getElementById("answer");
    // Compare answers as Strings since input box holds String.
    const checkRequest = new Request("game?start=" + startTime + "&ans=" + answer.value, {method: "POST"});
    const checkResponse = await fetch(checkRequest);
    const checkResponseText = await checkResponse.text();

    // If there was a server error, alert with text and exit.
    if (checkResponse.status >= 400) {
        alert(checkResponseText);
        return;
    }

    const getRequest = new Request("game?start=" + startTime, {method: "GET"});
    const getPromise = fetch(getRequest);

    score = parseInt(checkResponseText);
    scores.textContent = "Score: " + score;
    answer.value = "";

    // Update with next question.
    const getResponse = await getPromise;
    const getResponseText = await getResponse.text();

    // If there was an error during the get request, alert and exit.
    if (getResponse.status >= 400) {
        alert(getResponseText);
        return;
    }

    question.src = "data:image/png;base64, " + getResponseText;
}

// Reset score, remove question screen, go back to intro screen.
async function gameOver() {
    scores.textContent = "Score: " + score;
    answer.value = "";
    instructions.style.display = "inline";
    startBtn.style.display = "inline-block";
    question.style.display = "none";
    answer.style.display = "none";
    submit.style.display = "none";
    const name = prompt("Enter your name into the field below to be put on the highscore list. "
    + "If you do not wish to be entered, leave the field blank.");
    if (name !== null && name.length > 0) {
        const scoreRequest = new Request("game?start=" + startTime + "&name=" + name, {method: "POST"});
        const resposnse = await fetch(scoreRequest);

        // If there was a server error, alert and exit.
        if (response.status >= 400) {
            alert(await response.text());
            return;
        }
        loadScores();
    }
}

/**
 * Fetches scores from the server and adds them to the DOM.
 */
async function loadScores() {

    // Check if page field is valid.
    let page = $("#score-page").val();
    if (parseInt(page) != page || parseInt(page) <= 0) {
        alert("Invalid page");
        $("#score-page").val(1);
        page = 1;
    }

    // Get entry limit from form.
    const selectMax = document.getElementById("score-max");
    const maxScores = selectMax.options[selectMax.selectedIndex].value;

    let queryString = "max=" + maxScores;
    queryString += "&page=" + page;
  
    // Request JSON based on user entry limit.
    const scoresResponse = await fetch("/scores?" + queryString);

    // If there was a server error, alert with text and exit.
    if (scoresResponse.status >= 400) {
        alert(await scoresResponse.text());
        return;
    }

    // Convert JSON to object.
    const scores = await scoresResponse.json();

    // Add all scores to score container.
    const container = document.getElementById("score-container");
    container.innerHTML = "";
    scores.forEach(score => container.innerHTML += createScore(score).outerHTML);
}

$(document).ready(loadScores);

function createScore(entry) {
       
    // Set up div for a score.
    const scoreDiv = document.createElement("li");
    scoreDiv.setAttribute("class", "score-entry");
    scoreDiv.innerHTML = "";

    // Add fields.
    scoreDiv.appendChild(createSpan(entry.rank + ". " + entry.name));

    const score = createSpan(entry.score);
    score.setAttribute("class", "score");
    scoreDiv.appendChild(score);

    return scoreDiv;
}

/**
 * Create a paragraph with the given text.
 */
function createSpan(text) {
    const span = document.createElement("span");
    span.textContent = text;
    return span;
}

/**
 * Moves to next page of scores.
 */
function nextPage() {
    $("#score-page").val(parseInt($("#score-page").val()) + 1);
    loadScores();
}

/**
 * Moves to previous page of scores.
 */
function prevPage() {
    $("#score-page").val(parseInt($("#score-page").val()) - 1);
    loadScores();
}