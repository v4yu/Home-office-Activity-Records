//===================================================HTML_ELEMENTS================================================================================
const organisation_name_box = document.getElementById("org_name");
const organisation_description_box = document.getElementById("org_description");
const organisation_nip_box = document.getElementById("org_nip");
const organisation_regon_box = document.getElementById("org_regon");

const organisation_edit_btn = document.getElementById("organisationEditBtn");
const organisation_invite_btn = document.getElementById("organisationInviteBtn");

//===================================================FUNCTIONS====================================================================================
function enableOrganisationAdminOptions(){
    $(organisation_edit_btn).removeClass("d-none");
}

function disableOrganisationAdminOptions(){
    $(organisation_edit_btn).addClass("d-none");
}

function enableOrganisationHrOptions(){
    $(organisation_invite_btn).removeClass("d-none");
}

function disableOrganisationHrOptions(){
    $(organisation_invite_btn).addClass("d-none");
}
//===================================================CALLBACKS====================================================================================
function fillOrganisationDetials(response){
    organisation_name_box.value = response.name;
    organisation_description_box.value = response.description;
    organisation_nip_box.value = response.NIP;
    organisation_regon_box.value = response.REGON;
}
//===================================================EVENTS=======================================================================================
$(document).ready(function() {
    var requestArray = {}
    performRequest("OrganisationDetails",requestArray,fillOrganisationDetials);
});