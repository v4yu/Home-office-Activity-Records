//===================================================IMPORTS======================================================================================
window.jsPDF = window.jspdf.jsPDF;

//===================================================GLOBAL VARIABLES=============================================================================
var unitName = "";
var generatingPerson;
var organisationData = {};
//===================================================COPY PASTED================================================================================== 
  function createHeaders(keys) {
    var result = [];
    for (var i = 0; i < keys.length; i += 1) {
      result.push({
        id: keys[i],
        name: keys[i],
        prompt: keys[i],
        width: 62,
        align: "center",
        padding: 0
      });
    }
    return result;
  }
  
  var headers = createHeaders([
    "title",
    "category",
    "status",
  ]);

//===================================================FUNCTIONS====================================================================================
async function generatePdfReport(inputData){
    const doc = new jsPDF();

    var requestArray = {};
    

    await new Promise(r => performRequest("GetHierarchy",requestArray,function(response){unitName = response.name;r("")}));
    await new Promise(r => performRequest("UserDetails",requestArray,function(response){generatingPerson = response.name +" "+ response.surname;r("")}));
    await new Promise(r => performRequest("OrganisationDetails",requestArray,function(response){
      organisationData["name"] = response.name;
      organisationData["NIP"] = response.NIP;
      organisationData["REGON"] = response.REGON;
      r("");
    }));

    //background gradient
    doc.addImage("pictures/grzegorz_gradient.png", "JPEG", 0, 0, 220, 300);

    //Grzegorz logo
    doc.setFontSize(22);
    doc.setTextColor(255,255,255);
    doc.text("Grzegorz", 25, 20);
    doc.addImage("pictures/grzegorz.png", "JPEG", 10, 10, 13, 15);

    //Additional white box background
    doc.setDrawColor(0);
    doc.setFillColor(255, 255, 255);
    doc.roundedRect(30, 40, 150, 220, 3, 3, "F");

    //Title of report
    doc.setTextColor(0,0,0);
    doc.text("Tasks report\n for unit\n", 105, 80, null, null, "center");
    doc.setFont("Helvetica", "bold");
    doc.text(unitName, 105, 100, null, null, "center");

    //Nested green box background
    doc.setDrawColor(0);
    doc.setFillColor(242,243,248);
    doc.roundedRect(35, 145, 140, 110, 3, 3, "F");

    //Detials of report:
    //username
    doc.setFontSize(16);
    doc.setFont("Helvetica", "normal");
    doc.text("Generated by: ", 80, 160, null, null, "center");
    doc.setFont("Helvetica", "bold");
    doc.text(generatingPerson, 130, 160, null, null, "center");

    //organisation details
    doc.setFont("Helvetica", "normal");
    doc.text("Organisation: ", 80, 180, null, null, "center");
    doc.text("NIP: ", 80, 200, null, null, "center");
    doc.text("REGON: ", 80, 220, null, null, "center");
    doc.setFont("Helvetica", "bold");
    doc.text(organisationData["name"], 130, 180, null, null, "center");
    doc.text(organisationData["NIP"], 130, 200, null, null, "center");
    doc.text(organisationData["REGON"], 130, 220, null, null, "center");

    //organisation details
    const currentDate = new Date();
    const yearString = currentDate.getFullYear().toString();
    const monthString = (currentDate.getMonth()+1)<10?"0"+(currentDate.getMonth()+1):(currentDate.getMonth()+1);
    const dayString = currentDate.getDate().toString();
    const dataString = yearString + " " + monthString + " " + dayString;
    doc.setFont("Helvetica", "normal");
    doc.text("Generated on: ", 80, 240, null, null, "center");
    doc.setFont("Helvetica", "bold");
    doc.text(dataString, 130, 240, null, null, "center");

    doc.setFontSize(12);

    inputData.forEach(function(value, key) {
        doc.addPage("a4", "0");
        //background gradient
        doc.addImage("pictures/grzegorz_gradient.png", "JPEG", 0, 0, 220, 300);
        //Additional white box background
        doc.setDrawColor(0);
        doc.setFillColor(255, 255, 255);
        doc.roundedRect(30, 10, 150, 280, 3, 3, "F");
        doc.setFont("Helvetica", "bold");

        var assigneeName = "";

        if(value[0].assignedWorker!=null){
            assigneeName = value[0].assignedWorker.name + " " +  value[0].assignedWorker.surname;
        }else{
            assigneeName = value[0].assignedUnit.name + " (unit)";
        }

        var tempArray = new Array();

        value.forEach(function(element){

            tempArray.push({ title : element.title, category : element.category.name, status : castStatusToString(element.status)});
        })

        doc.text(assigneeName, 105, 20, null, null, "center");

        doc.table(35, 40, tempArray, headers, { autoSize: false });
    })

    doc.save("a4.pdf");
}

function castStatusToString(statusInt){
    switch(statusInt){
        case 0:
            return "TO DO"
        break;
        case 1:
            return "IN PROGRESS"
        break;
        case 2:
            return "DONE"
        break;
        case 3:
            return "COMPLETED"
        break;
        case 4:
            return "CANCELED"
        break;
    }
}