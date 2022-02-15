const express = require('express');
const morgan = require('morgan');
const getRouter = require("./requests/get");
const postRouter = require("./requests/post");

const app = express();


app.use(morgan('short')); //logging
app.use(express.static("./app"));
app.use(getRouter);
app.use(postRouter);



app.listen(process.env.PORT || 5000, function()
{
    console.log("I am listening...");
});

