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
    question.src = "data:image/png;base64, " + getResponseText;
    setTimeout(gameOver, 60000);
}

async function check() {
    const answer = document.getElementById("answer");
    // Compare answers as Strings since input box holds String.
    const checkRequest = new Request("game?start=" + startTime + "&ans=" + answer.value, {method: "POST"});
    const checkPromise = fetch(checkRequest);
    const checkResponse = await checkPromise;
    const checkResponseText = await checkResponse.text();

    const getRequest = new Request("game?start=" + startTime, {method: "GET"});
    const getPromise = fetch(getRequest);

    score = parseInt(checkResponseText);
    scores.textContent = "Score: " + score;
    answer.value = "";

    // Update with next question.
    const getResponse = await getPromise;
    const getResponseText = await getResponse.text();
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
    const addScore = confirm("Click OK to be added to the highscore list.");
    if (addScore) {
        const scoreRequest = new Request("game?start=" + startTime, {method: "POST"});
        await fetch(scoreRequest);
        loadScores();
    }
}

google.charts.load('current', {'packages':['corechart', 'table']});
google.charts.setOnLoadCallback(loadScores);

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
    const scoresJSON = await fetch("/scores?" + queryString);
    // Convert JSON to object.
    const scores = await scoresJSON.json();

    // Add all scores to score table.
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Name');
    data.addColumn('number', 'Score');
    scores.forEach(score => data.addRow([score.nickname, score.score]));

    const options = {
        showRowNumber: true,
        width: '100%',
        height: '100%'
    };

    const chart = new google.visualization.Table(
        document.getElementById('score-container'));
    chart.draw(data, options);
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