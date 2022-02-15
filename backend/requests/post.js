const express = require("express");
const router = express.Router();

const database = require("./../database");
const bodyParser = require("body-parser");

const urlencodedParser = bodyParser.urlencoded({extended: false});




router.post("/post/execute", urlencodedParser, function(req, res)
{
    var pool = database.getConnection();
    req.setTimeout(500000);

    pool.getConnection(function(conError, connection)
    {
        var params = req.body;
        var cmd = params.command;
    
        console.log("Main Queries: " + cmd);
        console.log("Attached Queries: " + req.body.attach);
    
        database.ExecutePostCommand(req.body, function(results)
        {
            for(var i = 0; i < results.length; i++)
            {
                if(results[i] != undefined)
                    cmd = cmd.split("[" + i + "]").join(results[i]);
                else
                cmd = cmd.split("[" + i + "]").join("0");
            }

            var commands = cmd.split(";");
            var currentCommand = 0;
            for(var i = 0; i < commands.length; i++)
            {
                if(commands[i] != "")
                {
                    connection.query(commands[i], function(err, rows, fields)
                    {
                        connection.release();

                        if(err)
                        {
                            console.log("Failed to post execute command: " + cmd + "\n" + err);
                            res.sendStatus(500);
    
                            connection.destroy();
                            return;
                        }
    
                        if(currentCommand == (commands.length - 2))
                        {
                            rows.affectedRows = currentCommand + 1;
                            res.json(rows);
                        }
                        else
                            currentCommand++;
    
                        
                        connection.destroy();
                    });
                }
            }
        });
    });

    
});

router.post("/insert", urlencodedParser, function(req, res)
{
    var connection = database.getConnection();

    var cmd = database.createInsertCommand(req.body);
    
    connection.query(cmd, function(err, rows, fields)
    {
        if(err)
        {
            console.log("Failed to insert to DB: " + err);
            res.sendStatus(500);

            connection.release();
            return;
        }

        connection.release();
        res.json(rows);
    })
});

router.post("/update", urlencodedParser, function(req, res)
{
    var connection = database.getConnection();

    var cmd = database.createUpdateCommand(req.body);
    
    connection.query(cmd, function(err, rows, fields)
    {
        if(err)
        {
            console.log("Failed to insert to DB: " + err);
            res.sendStatus(500);
            return;
        }

        res.json(rows);
    })
});

router.post("/delete", urlencodedParser, function(req, res)
{
    var connection = database.getConnection();

    var cmd = database.createDeleteCommand(req.body);
    
    connection.query(cmd, function(err, rows, fields)
    {
        if(err)
        {
            console.log("Failed to insert to DB: " + err);
            res.sendStatus(500);

            connection.release();
            return;
        }

        connection.release();
        res.json(rows);
    })
});


module.exports = router;