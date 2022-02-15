const mysql = require('mysql');

module.exports.getConnection = function()
{
    const pool = mysql.createPool({
        connectionLimit: 100,
        host: "us-cdbr-east-03.cleardb.com",
        user: "b47f6df1a33aa8",
        password: "0a2a235c",
        database: "heroku_0d20d1657bc891f",
        nestTables: true,
        query: { timeout: 600000 }
    });
    //const pool = mysql.createPool({
    //    connectionLimit: 1,
    //    host: "localhost",
    //    user: "root",
    //    password: "123qwe",
    //    database: "nafea_schema"
    //});

    return pool;
}




module.exports.ExecuteGetCommand = function(params, callback)
{
    if(!isParamExist(params.attach) || params.attach == "")
        callback(new Array());

    this.queryAttachedCommands(params.attach, callback);
}

module.exports.ExecutePostCommand = function(params, callback)
{
    if(!isParamExist(params.attach) || params.attach == "")
        callback(new Array());

    this.queryAttachedCommands(params.attach, callback);
}

module.exports.queryAttachedCommands = function(attachedCommands, callback)
{
    var connection = this.getConnection();
    var results = new Array();
    
    var attachedCmds = getParamValuesAsArray(attachedCommands);
    var currentCommand = 0;
    for(var i = 0; i < attachedCmds.length; i++)
    {
        var cmd = attachedCmds[i];
        connection.query(cmd, function(err, rows, fields)
        {
            if(err)
            {
                console.log("------------------------------------------------------------------------------");
                console.log("Failed to execute attached command: [" + i + "]: " + cmd + "\n" + err);
                console.log("------------------------------------------------------------------------------");
                return;
            }

            results[currentCommand] = rows[0].result;
            if(currentCommand == (attachedCmds.length - 1))
            {
                callback(results);
            }
            else
                currentCommand++;
        })
    }
}

function getParamValuesAsArray(param)
{   
    var paramArray = new Array();
    if(isParamExist(param))
    {
        paramArray = param.split(",");
    }

    return paramArray;
}

function getParamValuesAsCmdString(param, emptyReplacment)
{
    var cmd = "";
    
    if(isParamExist(param))
    {
        var paramString = param.substring(1, param.length - 1);
        var paramArray = paramString.split(",");


        if(paramArray.length == 0)
            cmd += emptyReplacment;
        else
        {
            for(var i = 0; i < paramArray.length; i++)
            {
                if(i == (paramArray.length - 1))
                    cmd += paramArray[i];
                else
                    cmd += paramArray[i] + ",";
            }
        }

        return cmd;
    }
    else
        return emptyReplacment;
}

function getConditionValue(query)
{
    var condition = query.cond;

    if(isParamExist(condition))
    {
        var conditionString = condition.substring(1, condition.length - 1);

        return " WHERE " + conditionString;
    }
    else
        return "";
}

module.exports.createQueryCommand = function(table, query)
{
    var cmd = "SELECT " + getParamValuesAsCmdString(query.attrs, "*");
    cmd += " FROM " + table;
    cmd += getConditionValue(query);

    return cmd;
}

module.exports.createInsertCommand = function(params)
{
    var cmd = "INSERT INTO ";
    cmd += params.table + " VALUES(" + params.values + ")";

    return cmd;
}

module.exports.createDeleteCommand = function(params)
{
    var cmd = "DELETE FROM ";
    cmd += params.table + " WHERE " + params.condition;

    return cmd;
}

module.exports.createUpdateCommand = function(params)
{
    var cmd = "UPDATE ";
    cmd += params.table + " SET " + params.valuesSet + " WHERE " + params.condition;

    return cmd;
}

function isParamExist(param)
{
    if(typeof param != 'undefined')
        return true;
    else
        return false;
}