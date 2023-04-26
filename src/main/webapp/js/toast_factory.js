//===================================================GLOBAL VARIABLES=============================================================================
var toastCounter = 0;
//===================================================HTML ELEMENTS================================================================================
const toast_box = document.getElementById("toastBox");

//===================================================FUNCTIONS====================================================================================
function insertToastError(title,message,time){
    $(toast_box).append('<div class="toast" id="test'+(++toastCounter)+'" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="'+(time==undefined?"false":"true")+'" data-bs-delay="'+parseInt(time)+'"><div class="toast-header"><img src="pictures/sad_penguin2.png" width="25" class="rounded me-2" ><strong class="me-auto text-danger">'+title+'</strong><button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button></div><div class="toast-body">'+message+'</div></div>');
    $("#test"+toastCounter).toast("show");
}

function insertToastInformation(title,message,time){
    $(toast_box).append( '<div class="toast" id="test'+(++toastCounter)+'" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="'+(time==undefined?"false":"true")+'" data-bs-delay="'+parseInt(time)+'"><div class="toast-header"><img src="pictures/grzegorz.png" width="25" class="rounded me-2" ><strong class="me-auto">'+title+'</strong><button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button></div><div class="toast-body">'+message+'</div></div>');
    $("#test"+toastCounter).toast("show");
}

function insertToastSuccess(title,message,time){
    $(toast_box).append('<div class="toast" role="alert" id="test'+(++toastCounter)+'" aria-live="assertive" aria-atomic="true" data-bs-autohide="'+(time==undefined?"false":"true")+'" data-bs-delay="'+parseInt(time)+'"><div class="toast-header"><img src="pictures/happy_penguin.png" width="25" class="rounded me-2" ><strong class="me-auto text-success">'+title+'</strong><button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button></div><div class="toast-body">'+message+'</div></div>');
    $("#test"+toastCounter).toast("show");
}