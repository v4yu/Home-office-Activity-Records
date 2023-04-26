//===================================================HTML_ELEMENTS================================================================================
const creat_unit_btn = document.getElementById("creatUnitBtn");
const unit_name_input = document.getElementById("unitName");
const parent_unit_name_box = document.getElementById("parentUnitNameBox");

//===================================================GLOBAL VARIABLES=============================================================================
var parentUnitId;

//===================================================FUNCTIONS====================================================================================
function getParentUnitId(){
    parentUnitId = parseInt(window.location.hash.substring(1));
}

function getNewUnitName(){
    return unit_name_input.value;
}

function getParentUnitName(unit){
    if(unit.id == parentUnitId){
        console.log(unit.name);
        return unit.name;
    }

    for(var i = 0;i<unit.children.length;i++){
        var temp = getParentUnitName(unit.children[i]);
        if(temp!=null){
            return temp;
        }
    }  

    return null;

}
//===================================================CALLBACKS====================================================================================
function creatunitFillUsername(response){
    const username = document.getElementById("username_field");
    var userName = response.name + " " + response.surname;
    username.innerHTML = userName;
}

function createunitCallback(response){

}

function getHierarchyCallback(response){
    console.log(response);
    parentUnitNameBox.innerHTML = getParentUnitName(response);
}
//===================================================EVENTS=======================================================================================
$(document).ready(function() {
    var requestArray = {};
    performRequest("UserDetails",requestArray,creatunitFillUsername);
    performRequest("GetHierarchy",requestArray,getHierarchyCallback);
    getParentUnitId();
});

$(creat_unit_btn).click(function(){
    var requestArray = {};
    requestArray["parent"] = parentUnitId
    requestArray["name"] = getNewUnitName();
    performRequest("CreateUnit",requestArray,createunitCallback);
});