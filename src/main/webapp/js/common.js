function performRequest(requestServlet,requestArray,readyStateCallback){
    const xhttp = new XMLHttpRequest();
    var requestJson = null;

    xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) { 
            console.log("Response from servlet '"+requestServlet+"':");
            console.log(this.response);

            try{
                var result = jQuery.parseJSON(this.response); 
            }catch{
                var result = {};
                result["status"] = "error";
                result["message"] = "Service not responding";
            }
            
            readyStateCallback(result);
        }
    }
    console.log("Sending (POST) request to servlet '"+requestServlet+"' with input data:");
    console.log(requestArray);

    if(Object.keys(requestArray).length>0){
        requestJson = JSON.stringify(requestArray);
    }

    xhttp.open("POST", requestServlet, true);
    xhttp.send(requestJson);
}


function performRequestGet(requestServlet,requestArray,readyStateCallback){
    const xhttp = new XMLHttpRequest();
    var requestServletwithArgs = requestServlet+"?";

    xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) { 
            console.log("Response from servlet '"+requestServlet+"':");
            console.log(this.response);

            try{
                var result = jQuery.parseJSON(this.response); 
            }catch{
                var result = {};
                result["status"] = "error";
                result["message"] = "Service not responding";
            }

            readyStateCallback(result);
        }
    }
    console.log("Sending (GET) request to servlet '"+requestServlet+"' with input data:");
    console.log(requestArray);
    
    if(Object.keys(requestArray).length>0){
        for (const [key, value] of Object.entries(requestArray)) {
            if(typeof value != 'undefined') {
                requestServletwithArgs+=key+"="+value+"&";
            }            
        }
    }
    console.log("Request arguments in url will be: "+requestServletwithArgs);

    xhttp.open("GET", requestServletwithArgs, true);
    xhttp.send(null);
}


function performRequestPlainString(requestServlet,requestString,readyStateCallback){
    const xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) { 
            console.log("Response from servlet '"+requestServlet+"':");
            console.log(this.response);

            try{
                var result = jQuery.parseJSON(this.response); 
            }catch{
                var result = {};
                result["status"] = "error";
                result["message"] = "Service not responding";
            }
            
            readyStateCallback(result);
        }
    }
    console.log("Sending (plain string) request to servlet '"+requestServlet+"' with input data:");
    console.log(requestString);
    
    if(requestString.length<=0){
        requestString = null;
    }

    xhttp.open("POST", requestServlet, true);
    xhttp.send(requestString);
}