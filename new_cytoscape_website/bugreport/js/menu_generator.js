$(document).ready(function () {
    appendMainMenuItems();
    appendIntro();
    appendDocument();
    appendCommunity();
});

function appendMainMenuItems() {
    $("#nav")
        .append("<li id=\"homeMenu\"><a href=\"http://www.cytoscape.org/\">Home</a></li>")
        .append("<li id=\"introMenu\"><a href=\"http://cytoscape.org\">Introduction</a></li>")
        .append("<li id=\"downloadMenu\"><a href=\"http://cytoscape.org/download.html\">Download</a></li>")
        .append("<li id=\"pluginMenu\"><a href=\"http://apps.cytoscape.org\">Apps</a></li>")
        .append("<li id=\"documentMenu\"><a href=\"http://cytoscape.org\">Documentation</a></li>")
        .append("<li id=\"communityMenu\"><a href=\"http://cytoscape.org\">Community</a></li>")
        .append("<li id=\"bugMenu\"><a href=\"http://chianti.ucsd.edu/cyto_web/bugreport/bugreport.php\">Report a Bug</a></li>")
        .append("<li id=\"helpMenu\"><a href=\"http://cytoscape.org/community.html\">Getting Help</a></li>");
}

function appendIntro() {
    $("#introMenu").append(
        "<ul><li><a href=\"http://cytoscape.org/what_is_cytoscape.html\">What is Cytoscape?</a></li>"
            + "<li><a href=\"http://cytoscape.org/who_is_using.html\">Who is Using Cytoscape?</a></li>"
            + "<li><a href=\"http://cytoscape.org/screenshots.html\">Screenshots</a></li></ul>");
}

function appendDocument() {
    $("#documentMenu").append(
        "<ul><li><a href=\"http://cytoscape.org/documentation_users.html\">for Users</a></li>"
            + "<li><a href=\"http://cytoscape.org/documentation_developers.html\">for Developers</a></li>"
            + "<li><a href=\"http://cytoscape.org/releasenotes.html\">Release Notes</a></li>"
            + "<li><a href=\"http://wiki.cytoscape.org/\">Cytoscape Wiki</a></li>"
            + "<li><a href=\"http://opentutorials.cgl.ucsf.edu/index.php/Portal:Cytoscape\">Cytoscape at Open Tutorials</a></li></ul>");
}

function appendCommunity() {
    $("#communityMenu")
        .append(
            "<ul><li><a href=\"http://cytoscape.org/development_team.html\">Developer Team</a></li>"
                + "<li><a href=\"http://cytoscape.org/community.html\">Social Media</a></li>"
                + "<li><a href=\"http://nrnb.org/cyretreat/\">Conferences</a></li>"
                + "<li><a href=\"http://nrnb.org/training.html\">Training</a></li>"
                + "</ul>");
}
