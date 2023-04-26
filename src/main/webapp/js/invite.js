//===================================================GLOBAL VARIABLES=============================================================================
var arrayOfInvitations;
var arrayOfInvitationsPaged = new Array();
var currentPageOfInvitations = 0;
//===================================================HTML ELEMENTS================================================================================
var fileInput = document.getElementById("csvInvitationFile");
var fileInputBox = document.getElementById("fileInputBox");
var parseInvitationsList = document.getElementById("parsedCsvDataList");
var parseInvitationsBox = document.getElementById("parsedDataBox");

//buttons
var acceptBulkBtn = document.getElementById("acceptBulkInvitations");
var rejectBulkBtn = document.getElementById("rejectBulkInvitations");
var checkBulkBtn = document.getElementById("checkAllInvitations");
var uncheckBulkBtn = document.getElementById("uncheckAllInvitations");

//List navigation
var nextPageBtn = document.getElementById("pageNext");
var prevPageBtn = document.getElementById("pagePrev");
var leftPageBtn = document.getElementById("leftPage");
var middlePageBtn = document.getElementById("middlePage");
var rightPageBtn = document.getElementById("rightPage");
var perPageSelect = document.getElementById("perPageSelect");
//===================================================FUNCTIONS====================================================================================
function checkAllInvitations(){
    arrayOfInvitationsPaged.forEach((innerArray) =>{
        innerArray.forEach((element) =>{
            element.choosen = true;
        }); 
    }); 
}

function uncheckAllInvitations(){
    arrayOfInvitationsPaged.forEach((innerArray) =>{
        innerArray.forEach((element) =>{
            element.choosen = false;
        }); 
    }); 
}

function switchViewToParsedData(){
    $(fileInputBox).addClass("d-none");
    $(parseInvitationsBox).removeClass("d-none");

}

function switchViewToFileInput(){
    $(fileInputBox).removeClass("d-none");
    $(parseInvitationsBox).addClass("d-none");
}

function clearInvitationsBox(){
    parseInvitationsList.innerHTML = "";
}

function clearChoosenCsv(){
    fileInput.value = null;
}

function changeRecordPerPage(newRecordsPerPage){
    divideBulkInvitationsOnPages(arrayOfInvitations,newRecordsPerPage);
    changeToPageNo(0);
}

function divideBulkInvitationsOnPages(inputArray,invitationsPerPage){
    var numberOfPage = 0;
    var numberOfInvitation = 0;
    arrayOfInvitationsPaged = new Array();

    inputArray.forEach((element) =>{
        if(arrayOfInvitationsPaged[numberOfPage]==undefined){
            arrayOfInvitationsPaged[numberOfPage] = new Array();
        }

        element["id"] = numberOfInvitation;
        if(element["choosen"] == undefined){
            element["choosen"] = true;
        }
        arrayOfInvitationsPaged[numberOfPage].push(element);

        if(arrayOfInvitationsPaged[numberOfPage].length>=invitationsPerPage){
            numberOfPage++;
        }

        numberOfInvitation++;
    });
    console.log(arrayOfInvitationsPaged);
}

function getConfirmationArray(){
    var tempArray = new Array();
    arrayOfInvitationsPaged.forEach((innerArray) =>{
        innerArray.forEach((element) =>{
            if(element.choosen)
                tempArray.push(element.id);
        }); 
    }); 

    return tempArray;
}

function displayPageNo(numberOfPage){
    arrayOfInvitationsPaged[numberOfPage].forEach((element) =>{
        parseInvitationsList.innerHTML+='<li class="list-group-item d-flex justify-content-between align-items-start"><div class="d-flex justify-content-between flex-grow-1"><div class="d-flex justify-content-between"><div class="align-self-center me-4">'+(element.id+1)+'</div><div class="d-flex flex-column"><div class="fw-bold">'+element.name+' '+element.surname+'</div>'+element.email+'</div></div><input type="checkbox" '+(element.choosen?'checked':'')+' class="invitationChoiceCheckboxs" value="'+element.id+'"></div></li>';
    });
}

function changeToPageNo(numberOfPage){
    if(numberOfPage<0){
        numberOfPage = 0;
    }
        
    if(numberOfPage>=arrayOfInvitationsPaged.length){
        numberOfPage = arrayOfInvitationsPaged.length-1;
    }
    currentPageOfInvitations = numberOfPage;
        
    clearInvitationsBox();
    displayPageNo(numberOfPage);
    updateNavigation();
}

function moveToNextPage(){
    currentPageOfInvitations++;
    changeToPageNo(currentPageOfInvitations);
}

function moveToPrevPage(){
    currentPageOfInvitations--;
    changeToPageNo(currentPageOfInvitations)   
}

function changeCheckedStatus(invitationNo,newState){
    arrayOfInvitationsPaged.forEach((innerArray) =>{
        innerArray.forEach((element) =>{
            if(element.id==invitationNo){
                element.choosen = newState;
                return;
            }
        }); 
    }); 
}

function updateNavigation(){
    switch(currentPageOfInvitations){
        case 0:
            leftPageBtn.innerHTML = currentPageOfInvitations+1;
            $(leftPageBtn).addClass("active");
            middlePageBtn.innerHTML = currentPageOfInvitations+2;
            $(middlePageBtn).removeClass("active");
            rightPageBtn.innerHTML = currentPageOfInvitations+3;
            $(rightPageBtn).removeClass("active");
        break;

        case arrayOfInvitationsPaged.length-1:
            $(leftPageBtn).removeClass("active");

            if(arrayOfInvitationsPaged.length-1 < 2){
                leftPageBtn.innerHTML = currentPageOfInvitations;

                middlePageBtn.innerHTML = currentPageOfInvitations+1;
                $(middlePageBtn).addClass("active"); 
            }else{
                leftPageBtn.innerHTML = currentPageOfInvitations-1;

                middlePageBtn.innerHTML = currentPageOfInvitations;
                $(middlePageBtn).removeClass("active");
            }

            rightPageBtn.innerHTML = currentPageOfInvitations+1;
            $(rightPageBtn).addClass("active");
        break;

        default:
            leftPageBtn.innerHTML = currentPageOfInvitations;
            $(leftPageBtn).removeClass("active");
            middlePageBtn.innerHTML = currentPageOfInvitations+1;
            $(middlePageBtn).addClass("active");
            rightPageBtn.innerHTML = currentPageOfInvitations+2;
            $(rightPageBtn).removeClass("active");
        break;
    }

    if(arrayOfInvitationsPaged.length<=2){
        $(rightPageBtn).addClass("d-none");
    }else{
        $(rightPageBtn).removeClass("d-none");
    }

    if(arrayOfInvitationsPaged.length<=1){
        $(middlePageBtn).addClass("d-none");
    }else{
        $(middlePageBtn).removeClass("d-none");
    }
}
//===================================================CALLBACKS====================================================================================
function sendCsvInputCallback(response){
    if(response.status=="ok"){
        arrayOfInvitations = response.people;
        divideBulkInvitationsOnPages(arrayOfInvitations, perPageSelect.value);
        clearInvitationsBox();
    
        changeToPageNo(currentPageOfInvitations);
        switchViewToParsedData();
        insertToastSuccess("CSV parsed","CSV data has been parsed successfully",5000);
    }else{
        insertToastError("CSV parsing failed",response.message,5000);
    }
}

function bulkInvitationsConfirmationCallback(response){
    if(response.status=="ok"){
        insertToastSuccess("Bulk invitations send","Bulk invitations have been send",5000);
        clearChoosenCsv();
        switchViewToFileInput();
    }else{
        insertToastError("Failed to send bulk invitations",response.message,5000);
    }
}
//===================================================EVENTS AND REQUESTS==========================================================================
$(fileInput).on("change", function(){
    const files = $(fileInput).prop('files');
    const reader = new FileReader();
    var csv_text;

    reader.addEventListener('load', event => {
        csv_text = event.target.result;
        performRequestPlainString("MassInvite",csv_text,sendCsvInputCallback)
    });

    reader.readAsText(files[0]);
});

$(rejectBulkBtn).click(function(){
    clearChoosenCsv();
    switchViewToFileInput();
});

$(acceptBulkBtn).click(function(){
    var requestArray = {};
    var confirmationArray = getConfirmationArray();
    console.log(confirmationArray);
    requestArray["confirmedInvites"] = confirmationArray;
    
    performRequest("ConfirmInvitation",requestArray,bulkInvitationsConfirmationCallback);
});

$(checkBulkBtn).click(function(){
    checkAllInvitations();
    changeToPageNo(currentPageOfInvitations-1);
});

$(uncheckBulkBtn).click(function(){
    uncheckAllInvitations();
    changeToPageNo(currentPageOfInvitations-1);
});

$(nextPageBtn).click(function(){
    moveToNextPage();
});

$(prevPageBtn).click(function(){
    moveToPrevPage();
});

$(perPageSelect).on("change",function(){
    changeRecordPerPage(this.value);
});

$(document).on('click', '.invitationChoiceCheckboxs', function(){
    changeCheckedStatus(this.value,this.checked);
});

$(".navigationNumberBtn").click(function(){
    changeToPageNo(parseInt(this.innerHTML-1));
})