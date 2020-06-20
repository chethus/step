async function checkUser() {
    const responseJSON = await fetch("/login");
    const response = await responseJSON.json();
    if (!response.loggedIn) {
        location.href = response.url;
    } else {
        $("#nickname-form").css("display", "block");
        $("#nickname-submit").css("display", "inline-block");
    }
}
$(document).ready(checkUser);

async function submitNickname() {
    const nicknameForm = document.getElementById("nickname-form");
    const formData = (new URLSearchParams(new FormData(nicknameForm))).toString();
    const request = new Request("/user?" + formData, {method: "POST"});
    const response = await fetch(request);
    alert("Display name changed to " + $("#nickname").val() + ".");
}