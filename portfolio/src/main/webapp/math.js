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
function gameOver() {
    scores.textContent = "Score: " + score;
    answer.value = "";
    instructions.style.display = "inline";
    startBtn.style.display = "inline-block";
    question.style.display = "none";
    answer.style.display = "none";
    submit.style.display = "none";
}