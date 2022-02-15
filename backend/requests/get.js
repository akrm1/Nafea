const express = require("express");
const router = express.Router();

const database = require("./../database");



router.get("/get/execute/:command", function(req, res)
{
    var pool = database.getConnection();

    pool.getConnection(function(conError, connection)
    {
        if(conError)
            throw conError;
        
        var cmd = req.params.command;
        database.ExecuteGetCommand(req.query, function(results)
        {

            for(var i = 0; i < results.length; i++)
            {
                cmd = cmd.split("[" + i + "]").join(results[i]);
            }


            connection.query(cmd, function(err, rows, fields)
            {
                connection.release();

                if(err)
                {
                    console.log("Failed to query: " + cmd + "\n" + err);
                    res.sendStatus(500);

                    connection.destroy();
                    return;
                }
            
                res.json(rows);
                connection.destroy();
            });
        });
    });
});

router.get("/get/:table", function(req, res)
{
    var pool = database.getConnection();

    pool.getConnection(function(conError, connection)
    {
        var table = req.params.table;

        var cmd = database.createQueryCommand(table, req.query);
    
        connection.query(cmd, function(err, rows, fields)
        {
            connection.release();

            if(err)
            {
                console.log("Failed to query DB: " + err);
                res.sendStatus(500);

                connection.destroy();
                return;
            }
    
            res.json(rows);
            connection.destroy();
        });
    });

});

router.get("/", function(req, res)
{
    res.send("Welcome to Nafea Database Backend");
});


module.exports = router;