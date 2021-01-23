var _ = require("lodash");
var express = require("express");
var path = require("path");

var bodyParser = require("body-parser");
var app = express();

app.use(bodyParser.json());
app.use(
    bodyParser.urlencoded({
        // to support URL-encoded bodies
        extended: true
    })
);

//JS and CSS folder for embedding dashboards with requireJS
app.use(express.static(__dirname + "/html/app"));

app.get("/", function(req, res) {
    res.send("Hello World!");
});

app.get("/html/index", function(req, res) {
    res.sendFile(path.join(__dirname + "/html/index.html"));
});


app.get("/authentication/detail", function(req, res) {
    const token = req.headers.token;
    console.log('Token received: ', token);
    const payload = {
        active: (token === "12345" ? true : false),
        token: (token === "12345" ? token : null),
        username: "fernandommota",
        roles: [
            "Authenticated", "Power User", "Administrator"
        ]
    }

    res.send(payload);
});

app.listen(3000, function() {
    console.log("Embed Pentaho Server API Node Example app listening on port 3000!");
});