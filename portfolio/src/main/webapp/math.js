let highscore = 0;
let score = 0;
let correctAnswer = 0;
let start;
let instructions;
let question;
let scores;

window.onload = function setUpGame() {
    start = document.getElementById("start");
    instructions = document.getElementById("instructions");
    question = document.getElementById("question");
    answer = document.getElementById("answer");
    submit = document.getElementById("submit");
    scores = document.getElementById("scores");
}

function startGame() {
    question.textContent = generateQ();
    instructions.style.display = "none";
    start.style.display = "none";
    question.style.display = "inline";
    answer.style.display = "inline";
    submit.style.display = "inline-block";
    scores.style.display = "inline";
    setTimeout(gameOver, 60000);
}

function check() {
    const answer = document.getElementById("answer");
    if (answer.value === correctAnswer + "") {
        score ++;
        highscore = Math.max(score, highscore);
    }
    scores.textContent = "Score: " + score + "     Highscore: " + highscore;
    answer.value = "";
    question.textContent = generateQ();
}

function generateQ() {
    const type = Math.floor(Math.random() * 3);
    let a;
    let b;
    switch (type) {
        case 0:
            a = Math.floor(Math.random() * 1000);
            b = Math.floor(Math.random() * 1000);
            correctAnswer = a + b;
            return a + "+" + b;
        case 1:
            a = Math.floor(Math.random() * 1000);
            b = Math.floor(Math.random() * 1000);
            correctAnswer = a - b;
            return a + "-" + b;
        default:
            a = Math.floor(Math.random() * 100);
            b = Math.floor(Math.random() * 100);
            correctAnswer = a * b;
            return a + "*" + b;
    }
}

function gameOver() {
    score = 0;
    scores.textContent = "Score: " + score + "     Highscore: " + highscore;
    answer.value = "";
    instructions.style.display = "inline";
    start.style.display = "inline-block";
    question.style.display = "none";
    answer.style.display = "none";
    submit.style.display = "none";
}