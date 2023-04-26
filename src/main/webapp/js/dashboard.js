//===================================================HTML_ELEMENTS================================================================================
const organisations_list_tag = document.getElementById("organisations_list");
const current_organisation_name = document.getElementById("current_organisation");

const username = document.getElementById("username_field");
//===================================================VARIABLES====================================================================================
var organisations_map = new Map();
var currentOrganisationId = 0;

var userName;
//===================================================EVENTS=======================================================================================
$(document).ready(function() {
  var requestArray = {};
  performRequest("GetOrganisations",requestArray,dashboardGetOrganisationsCallback);
  performRequest("UserDetails",requestArray,dashboardFillUsername);
  performRequest("GetActions",requestArray,getActionsCallback);
});
//===================================================CALLBACKS====================================================================================
function dashboardGetOrganisationsCallback(response){
  insertOrganisation(response.organisations);
  markCurrentOrganisation();
}

function dashboardFillUsername(response){
  userName = response.name + " " + response.surname;
  username.innerHTML = userName;
}

function getActionsCallback(response){
  if(response.manager || response.admin){
    enableTasksManagerOptions();
  }

  if(response.dictionary || response.admin){
    enableDictionaryEditorOptions();
  }else{
    disableDictionaryEditorOptions();
  }

  if(response.admin || response.hr){
    enableOrganisationHrOptions();
  }else{
    disableOrganisationHrOptions();
  }

  if(response.admin){
    enableOrganisationAdminOptions();
  }else{
    disableOrganisationAdminOptions();
  }
}
//===================================================FUNCTIONS====================================================================================
function insertOrganisation(organisationsArray){ 
    for (var i=0;i<organisationsArray.length;i++) {
        organisations_map.set(organisationsArray[i].id,organisationsArray[i].name);
        //category_select.append(new Option("-","default"),undefined);
        $("#organisations_list").prepend('<li><a class="dropdown-item" id="'+organisationsArray[i].id+'" onclick="pickOrganisation(this.id)">'+organisationsArray[i].name+'</a></li>');
        //organisations_list_tag.innerHTML+='<div class="mx-1"><div class="card" ><div class="card-body"><h5 class="card-title">'+organisationsArray[i].name+'</h5><p class="card-text">Just a placeholder</p><button id="'+organisationsArray[i].id+'" class="btn btn-grzegorz-dark-green" onclick="pickOrganisation(this.id)" type="button" >Change</button></div></div></div>';
      }
  }


function pickOrganisation(organisationId){
  var current_url = document.URL;
  var url_w_organisation;

  let organisation_id_index = current_url.lastIndexOf("Organisation/");

  if(organisation_id_index==-1){
    let last_slash_index = current_url.lastIndexOf("/");
    url_w_organisation = current_url.slice(0, last_slash_index) + "/Organisation/"+ organisationId + current_url.slice(last_slash_index);
  }else{
    url_w_organisation = current_url.slice(0, organisation_id_index+"Organisation/".length) + organisationId + "/Dashboard";
  }

  window.location.href = url_w_organisation;

}

function markCurrentOrganisation(){
  var current_url = document.URL;
  var organisation_id_index = current_url.lastIndexOf("Organisation/")+"Organisation/".length;
  currentOrganisationId = current_url.substring(organisation_id_index,current_url.indexOf("/",organisation_id_index+1));

  var currentOrganisationName = organisations_map.get(parseInt(currentOrganisationId));
  if(currentOrganisationName != undefined){
    current_organisation_name.innerHTML = currentOrganisationName
  }else{
    current_organisation_name.innerHTML = "Pick Organisation";
  }
}