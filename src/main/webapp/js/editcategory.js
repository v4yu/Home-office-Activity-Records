//===================================================GLOBAL VARIABLES=============================================================================
var hashed_category_id;
var category_object = new Object();
//===================================================HTML_ELEMENTS================================================================================
const category_name = document.getElementById("name");
const category_description = document.getElementById("description");

const save_btn = document.getElementById("category_save_btn");
//===================================================FUNCTIONS====================================================================================

function returnNewCategoryData(){
    var requestArray = {};
    requestArray["name"] = category_name.value;
    requestArray["description"] = category_description.value;
    requestArray["id"] = category_object.id;
    
    return requestArray;
}

function fillCategoryData(){
    category_name.value = category_object.name;
    category_description.value = category_object.description;
}
//===================================================CALLBACKS====================================================================================
function getCategoriesCallback(response){
    var categoriesArray = response.categories;

    categoriesArray.forEach((element) =>{
        if(element.id == hashed_category_id){
            category_object = element;
            fillCategoryData();
            return;
        }
    });
}

function editCategoryCallback(response){
    if(response.status=="ok"){
        insertToastSuccess("Category edit success","New category data has been saved",5000);
    }else{
        insertToastError("Category edit error","New category data could not be saved",5000);
    }
}

function FillUsername(response){
    const username = document.getElementById("username_field");
    username.innerHTML = response.name + " " + response.surname;
  }
//===================================================EVENTS=======================================================================================

$(document).ready(function() {
    var requestArray = {};
    performRequest("UserDetails",requestArray,FillUsername);

    hashed_category_id = parseInt(window.location.hash.substring(1));
    requestCategories();
});

function requestCategories(){
    var requestArray = {};
    performRequest("GetCategories",requestArray,getCategoriesCallback);
}

$("#category_save_btn").click(function(){
    var requestArray = returnNewCategoryData();
    performRequest("EditCategory",requestArray,editCategoryCallback);
});
  
