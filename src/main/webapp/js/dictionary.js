//===================================================GLOBAL VARIABLES=============================================================================
var arrayOfCategories = {};
var isDictionaryEditor = false;
//===================================================HTML_ELEMENTS================================================================================
const categories_input = document.getElementById("categories_search_input");
const categories_input_box = document.getElementById("categoryInputBox");
const categories_list = document.getElementById("categories_search_list");
const create_category_btn = document.getElementById("createCategoryBtn");
//===================================================FUNCTIONS====================================================================================

//inserts categories from provided array
function insertCategories(categoriesArray){
    categories_list.innerHTML = "";
    categoriesArray.forEach((element) =>{
        categories_list.innerHTML+='<li class="list-group-item d-flex justify-content-between align-items-start"><div class="ms-2 me-auto d-flex justify-content-between flex-grow-1"><div class="d-flex flex-column justify-content-between"><div class="fw-bold">'+element.name+'</div>'+element.description+'</div>'+(isDictionaryEditor?'<button type="button" class="btn btn-grzegorz-dark-green col-4" id="'+element.id+'" onclick="location.href=\'CategoryEdit#\'+this.id">Edit</button>':'')+'</div></li>';
    });
}

function filterCategories(search_phrase){
    var resultCategoriesArray = new Array();
    search_phrase = search_phrase.toLowerCase().trim();
    arrayOfCategories.forEach((element) =>{
        if(element.name.toLowerCase().includes(search_phrase) || element.description.toLowerCase().includes(search_phrase)){
            resultCategoriesArray.push(element);
        }
    });
    return resultCategoriesArray;
}

function getSearchPharse(){
    return categories_input.value;
}

function enableDictionaryEditorOptions(){
    isDictionaryEditor = true;
    $(create_category_btn).removeClass("d-none");
    $(categories_input_box).removeClass("col-12");
    $(categories_input_box).addClass("col-9");
}

function disableDictionaryEditorOptions(){
    isDictionaryEditor = false;
    $(create_category_btn).addClass("d-none");
    $(categories_input_box).addClass("col-12");
    $(categories_input_box).removeClass("col-9");
}
//===================================================CALLBACKS====================================================================================

function getCategoriesCallback(response){
    arrayOfCategories = response.categories;
    insertCategories(arrayOfCategories);
}

//===================================================EVENTS=======================================================================================

//getting categories into local array
$(document).ready(function() {
    var requestArray = {}
    performRequest("GetCategories",requestArray,getCategoriesCallback);
});

//
$("#categories_search_input").on("keyup",function(){
    var new_search_phrase = getSearchPharse();
    var filteredCategoriesArray = filterCategories(new_search_phrase);
    insertCategories(filteredCategoriesArray);
});