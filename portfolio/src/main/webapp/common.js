/**
 * Load comment form based on login status.
 */
async function checkLogin() {

    // Request user information from server.
    const responseJSON = await fetch("/login");
    const response = await responseJSON.json();

    // If the user is not logged in, the server responds with a login url.
    if (!response.loggedIn) {

        // Unhide a login button with the login url.
        $(".login").css("display", "inline-block");
        $(".login").attr("onclick","location.href=\"" + response.url + "\"");

        $("#login-nav-link").css("display", "block");
        $("#login-nav-link").attr("href", response.url);
    } else {

        // Otherwise, unhide the comment form.
        $("#comment-form").css("display", "block");
        $("#comment-submit").css("display", "inline-block");

        $("#logout-nav-link").css("display", "block");
        $("#logout-nav-link").attr("href", response.url);
        $("#nickname-nav-link").css("display", "block");

        $("#start").css("display", "inline-block");
    }
}

$(document).ready(checkLogin);