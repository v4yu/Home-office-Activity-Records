//===================================================GLOBAL VARIABLES=============================================================================
var arrayOfListsIds = new Array("toDoTaskList","inProgressTaskList","doneTaskList","completedTaskList","canceledTaskList");


var currentTask= {id: 0, assignee:{assigneeId: 0,assigneeName:"",type: ""}};
var movedTo = 0;

var taskArray = {};
var currentlyFilteredTaskArray = {};
var workersArray = {};
var unitsArray = {};

var isManager = false;
var userId;
//===================================================HTML ELEMENTS================================================================================
const comment_textbox = document.getElementById("comment_textbox");

//Tasks
const tasks_col_cont = document.getElementById("taskColumnsMainContainer");

//Details 
const task_offcanvas = document.getElementById("taskDetailsOffcanvas");
const details_task_description = document.getElementById("task_description");
const details_task_name = document.getElementById("task_name");
const details_task_date = document.getElementById("creation_date");
const details_task_assignee = document.getElementById("assignee");
const details_task_reassign_btn = document.getElementById("reassignTaskbtn");
const details_task_rejection_box = document.getElementById("rejectionInfoBox");
const details_task_rejection_header = document.getElementById("rejectionHeader");
const details_task_rejection_reason = document.getElementById("rejectionDescription");

//Additional detials actions
const details_task_additional_actions_box = document.getElementById("additionalTaskActionBox");
const details_task_accept_btn = document.getElementById("acceptTaskbtn");
const details_task_reject_btn = document.getElementById("rejectTaskbtn");
const details_task_cancel_btn = document.getElementById("cancelTaskbtn");

//Reject modal
const reject_task_modal = document.getElementById("rejectTask");
const confirm_rejection_btn = document.getElementById("confirmRejectionBtn");
const reject_reason_input = document.getElementById("rejectReasonTextbox");

//Reassign modal
const re_assignee = document.getElementById("re_assignee_select");
const re_assignee_unit = document.getElementById("re_assignee_select_unit");
const confirm_reassignment_btn = document.getElementById("re_assignee_btn");

//Create task 
const create_task_modal = document.getElementById("createTaskModal");
const assignee_select = document.getElementById("assignee_select");
const assignee_select_unit = document.getElementById("assignee_select_unit");
const task_name_input = document.getElementById("task_name_input");
const task_description_input = document.getElementById("description_input");

//Filters
const filters_box = document.getElementById("filter_box");
const tasks_search_btn = document.getElementById("searchTasks");
const status_dropdown = document.getElementById("statusDropdown");
const tasks_search_keyword_input = document.getElementById("keywordSearchInput");
const tasks_search_assignee_select = document.getElementById("assigneeDropdown");
const tasks_search_hierarchy_checkbox = document.getElementById("hierarchyCheckbox");
const tasks_search_hierarchy_depth = document.getElementById("hierarchyDepthInput");
const tasks_search_start_date_input = document.getElementById("startingDateInput");
const tasks_search_end_date_input = document.getElementById("endingDateInput");
const tasks_search_category_select = document.getElementById("categoryModeSelect");
const tasks_show_hidden_checkbox = document.getElementById("hidenCheckbox");

const generate_report_btn = document.getElementById("generateReportBtn");
//===========================================================================================================
  function disableMovingButtons(disabled){
    var moves_array = $('#moving_list > li').children().toArray();
    for(var i=0;i<moves_array.length;i++){
      if(i==disabled){
        moves_array[i].classList.add("disabled");
      }else{
        moves_array[i].classList.remove("disabled");
      }
    }
  }
  function sortComments(commentsArray){
    var compareNumbers = function(a,b){
      const a_date = new Date(a.date+"T"+a.time+"Z");
      const b_date = new Date(b.date+"T"+b.time+"Z");
      return b_date-a_date;

    }
    commentsArray.sort(compareNumbers);
  }
  function insertComments(commentsArray){
    sortComments(commentsArray);
    var comments_section = document.getElementById("comments");
    comments_section.innerHTML = "";
    for (var i=0;i<commentsArray.length;i++) {
        comments_section.innerHTML+='<div class="mb-4"><div class="d-flex justify-content-between mb-1 text-blueish-grey"><div>'+commentsArray[i].worker.name+' '+commentsArray[i].worker.surname+'</div><div>'+commentsArray[i].date+'</div><div>'+commentsArray[i].time+'</div></div><div>'+commentsArray[i].text+'</div></div>';
      }
  }
  function createNewTask(){
    var requestArray = {}

    requestArray["title"] = task_name_input.value;
    requestArray["description"] = task_description_input.value;
    requestArray["assignedWorker"] = parseInt(assignee_select.value);
    requestArray["assignedUnit"] = parseInt(assignee_select_unit.value);
    requestArray["category"] = parseInt(task_category.value);
                
    performRequest("CreateTask",requestArray,createTaskCallback)
  }

  function fillCategories(){
    const xhttp = new XMLHttpRequest();
         
         xhttp.onreadystatechange = function() {
         if (this.readyState == 4 && this.status == 200) {  
           var result = jQuery.parseJSON(this.response);
   
           var categoriesArray = result.categories;
   
           console.log(categoriesArray);
   
           const category_select = $("#task_category");
   
           category_select.empty();
   
           category_select.append(new Option("-","default"),undefined);
   
           for (var i=0;i<categoriesArray.length;i++) {
             let newOption = new Option(categoriesArray[i].name+" ("+categoriesArray[i].description+")",categoriesArray[i].id);
             category_select.append(newOption,undefined);
            }

          fillFilterCategories(categoriesArray);
   
         }
       }
   
       xhttp.open("POST", "GetCategories", true);
       xhttp.send(null);
  }

//===================================================FUNCTIONS====================================================================================
function getArrayPerWorker(){
  var resultArray = new Map();

  currentlyFilteredTaskArray.forEach((element) =>{
    var assigneeId = "";

    if(element.assignedWorker!=null){
      assigneeId = element.assignedWorker.id;
    }else{
      assigneeId = element.assignedUnit.id;
    }

    if(!resultArray.get(assigneeId)){
      resultArray.set(assigneeId,new Array(element));
    }else{
      resultArray.get(assigneeId).push(element);
    }
    
  });

  return resultArray;
}

function getStatusToShow(){
  var resultArray = new Array();
  $(status_dropdown).children('li').each(function (element) {
    if($(this.firstChild).hasClass("active")){
      resultArray.push(element);
    }     
  });
  if(resultArray.length==0)
    resultArray = [0,1,2,3,4];

  return resultArray;
}

function getFiltredTasks(){
  var requestArray = {}

  requestArray["useHierarchy"] = tasks_search_hierarchy_checkbox.checked;
  var hierarchyDepth = parseInt(tasks_search_hierarchy_depth.value);
  requestArray["hierarchyDepth"] = Number.isInteger(hierarchyDepth) ? hierarchyDepth : 0;

  requestArray["dateFrom"] = tasks_search_start_date_input.value!="" ? tasks_search_start_date_input.value : undefined;
  requestArray["dateTo"] = tasks_search_end_date_input.value!="" ? tasks_search_end_date_input.value : undefined;

  performRequestGet("TaskList",requestArray,requestFilteredTaskListCallback);
}

function enableTasksManagerOptions(){
  isManager = true;
  getFiltredTasks();
  $(filters_box).removeClass("d-none");
  $(details_task_reassign_btn).removeClass("d-none");
  $(details_task_additional_actions_box).removeClass("d-none");

  if(tasks_show_hidden_checkbox.checked)
    showAdditionalTaskColumns();
}

function enableAdditionalTaskActions(){
  $(details_task_accept_btn).removeClass("d-none");
  $(details_task_reject_btn).removeClass("d-none");
}

function disableAdditionalTaskActions(){
  $(details_task_accept_btn).addClass("d-none");
  $(details_task_reject_btn).addClass("d-none");
}


function showAdditionalTaskColumns(){
  $(tasks_col_cont).removeClass("row-cols-3");
  $(tasks_col_cont).addClass("row-cols-5");

  arrayOfListsIds.forEach((element) =>{
    $('#'+element).parent().parent().removeClass("d-none");
  });
}

function hideAdditionalTaskColumns(){
  $(tasks_col_cont).removeClass("row-cols-5");
  $(tasks_col_cont).addClass("row-cols-3");
  
  $('#'+arrayOfListsIds[3]).parent().parent().addClass("d-none");
  $('#'+arrayOfListsIds[4]).parent().parent().addClass("d-none");

}

function showHashedTask(){
  var task_id = window.location.hash.substring(1);
  if(task_id!=""){
    getDetails(task_id);
    if(!$(task_offcanvas).hasClass("show")){
      var offcanvas = new bootstrap.Offcanvas(task_offcanvas, {backdrop: true});
      offcanvas.show();
    }
  }
}

function addHashTask(task_id){
  window.location.hash = task_id;
}

function removeTaskHash(){
  window.location.hash = "";
}

function clearTasks(){
   for (var i=0;i<arrayOfListsIds.length;i++) {
    var column = document.getElementById(arrayOfListsIds[i]);
    column.innerHTML = "";
  } 
}

function insertTasks(tasksArray){
  clearTasks();

  for (var i=0;i<tasksArray.length;i++) {
    var columnid = arrayOfListsIds[tasksArray[i].status]
    var column = document.getElementById(columnid);
    column.innerHTML+='<li class="list-group-item rounded-4 mb-1 p-3"><div class="h5 text-center text-break" id="taskBox'+tasksArray[i].id+'">'+tasksArray[i].title+'</div><hr><div class="d-flex justify-content-between mt-2"><div class="text-muted fs-7 align-self-center">'+tasksArray[i].category.name+'</div><button class="btn btn-grzegorz-dark-green" type="button" id="'+tasksArray[i].id+'" onclick="addHashTask(this.id)">View</button></div></li>'
  }
  updateTaskCounters(); 
}

function updateTaskCounters(){
  arrayOfListsIds.forEach((element) =>{
    var tasklist = document.getElementById(element);
    var li_array = tasklist.getElementsByTagName("li");
    var task_counter = document.getElementById(element+"Counter");
    task_counter.innerHTML = li_array.length;
  });
}

function fillReassignables(workers_array, units_array){
  $(re_assignee).empty();
  $(re_assignee_unit).empty();

  re_assignee.append(new Option("-","default"),undefined);
  re_assignee_unit.append(new Option("-","default"),undefined);

  for (var i=0;i<workers_array.length;i++){
    re_assignee.append(new Option(workers_array[i].name+" "+workers_array[i].surname,workers_array[i].id));
  }

  for (var i=0;i<units_array.length;i++){
    re_assignee_unit.append(new Option(units_array[i].name+" (unit)",units_array[i].id));
  }
}

function fillFilterCategories(categories_array){
  $(tasks_search_category_select).empty();

  tasks_search_category_select.append(new Option("-","default"),undefined);

  for (var i=0;i<categories_array.length;i++){
    tasks_search_category_select.append(new Option(categories_array[i].name,categories_array[i].id));
  }
}

function fillFilterAssignee(workers_array, units_array){
  $(tasks_search_assignee_select).empty();

  tasks_search_assignee_select.append(new Option("-","default"),undefined);

  for (var i=0;i<workers_array.length;i++){
    tasks_search_assignee_select.append(new Option(workers_array[i].name+" "+workers_array[i].surname,workers_array[i].id));
  }

  for (var i=0;i<units_array.length;i++){
    tasks_search_assignee_select.append(new Option(units_array[i].name+" (unit)",units_array[i].id));
  }
}

function selectCurrentlyAssignedWorker(){
  var temp_assignee;

  re_assignee.disabled = true;
  re_assignee_unit.disabled = true;

  $(re_assignee).children()
     .removeAttr('selected')
     .filter('[value=default]')
         .attr('selected', true);

  $(re_assignee_unit).children()
  .removeAttr('selected')
  .filter('[value=default]')
      .attr('selected', true);


  if(currentTask.assignee.type=="worker"){
    temp_assignee = re_assignee;
  }

  if(currentTask.assignee.type=="unit"){
    temp_assignee = re_assignee_unit;
  }
  temp_assignee.disabled = false;
  $(temp_assignee).children()
     .removeAttr('selected')
     .filter('[value='+currentTask.assignee.assigneeId+']')
         .attr('selected', true);
}
//===================================================CALLBACKS====================================================================================

function createTaskCallback(response){
  if(response.status=="ok"){
    insertToastSuccess("Task created","Task has been created",5000);
    getFiltredTasks()
    addHashTask(response.id);
    $(create_task_modal).modal('hide');
  }else{
    insertToastError("Failed to create new task",response.message,5000);
  }
}
function reassignTaskCallback(response){
  if(response.status=="ok"){
    insertToastSuccess("Task reassignment","Task has been reassign",5000);
  }else{
    insertToastError("Failed to reassign task",response.message,5000);
  }
}

//callback that gets TaskList response and call function that inserts task to html
function gettingTaskListCallback(response){
  taskArray = response.tasks;
  insertTasks(response.tasks);
}

//callback that gets AddComment response and if status is ok it once again requests all the details of current task
function addCommentCallback(response){
  var comment_textbox = document.getElementById("comment_textbox");
  if(response.status=="ok"){
    comment_textbox.value = "";
    getDetails(currentTask.id);
    insertToastSuccess("Comment added","Your comment has been added",5000);
  }else{
    insertToastError("Failed to add comment",response.message,5000);
  }
}
function moveTaskCallback(response){
  if(response.status=="ok"){
    insertToastSuccess("Task moving","Task has been moved",5000);
    $("#"+currentTask.id).parent().parent().detach().appendTo('#'+arrayOfListsIds[movedTo]);
    disableMovingButtons(movedTo);
    updateTaskCounters();
    getDetails(currentTask.id);

    if(isManager)
      getFiltredTasks();
  }else{
    insertToastError("Failed to move task",response.message,5000);
  }
}

function moveTaskCallbackFiltered(response){
  if(response.status=="ok"){
    insertToastSuccess("Moving task","Task has been moved",5000);
    $("#"+currentTask.id).parent().parent().detach().appendTo('#'+arrayOfListsIds[movedTo]);
    disableMovingButtons(movedTo);
    updateTaskCounters();
    getDetails(currentTask.id);
    $(reject_task_modal).modal('hide');
    reject_reason_input.value = ""
    getFiltredTasks();
  }else{
    insertToastError("Failed to move task",response.message,5000);
  }
}

function getAssignablesCallback(response){

  workersArray = response.workers;
  unitsArray = response.units;

  const assignee_select_jq = $(assignee_select);
  const assignee_select_unit_jq = $(assignee_select_unit);

  assignee_select_jq.empty();
  assignee_select_unit_jq.empty();

  assignee_select_jq.append(new Option("-","default"),undefined);
  assignee_select_unit_jq.append(new Option("-","default"),undefined);

  for (var i=0;i<workersArray.length;i++) {
    let newOption = new Option(workersArray[i].name+" "+workersArray[i].surname,workersArray[i].id);
    assignee_select_jq.append(newOption,undefined);

    if(userName == workersArray[i].name+" "+workersArray[i].surname){
      userId = workersArray[i].id;
    }
  }

  for (var i=0;i<unitsArray.length;i++) {
    let newOption = new Option(unitsArray[i].name+" (unit)",unitsArray[i].id);
    assignee_select_unit_jq.append(newOption,undefined);
  }

  fillReassignables(workersArray,unitsArray);
  fillFilterAssignee(workersArray,unitsArray);
  
}

function filterTasks(currentTaskArray, searchedString, assigneeId,searchedCategory,statusArray){
  var tempArray = new Array();
  var currentAssigneeId = 0;
  searchedString = searchedString.toLowerCase();

  currentTaskArray.forEach((element) =>{
    try{
      currentAssigneeId = element.assignedWorker.id;
    }catch{}
    try{
      currentAssigneeId = element.assignedUnit.id;
    }catch{}

    if(statusArray.includes(element.status) && element.title.toLowerCase().includes(searchedString) && (currentAssigneeId==assigneeId || assigneeId=="default") && (element.category.id==searchedCategory || searchedCategory=="default")){
      tempArray.push(element);
    }
  });

  return tempArray;
}

function requestFilteredTaskListCallback(response){
  taskArray = response.tasks;

  var newTaskArray;

  var searchKeyword = tasks_search_keyword_input.value;
  var assigneeId = tasks_search_assignee_select.value;
  var categoryId= tasks_search_category_select.value;
  var statusArray = getStatusToShow();
  newTaskArray = filterTasks(taskArray,searchKeyword,assigneeId,categoryId,statusArray);

  insertTasks(newTaskArray);
  currentlyFilteredTaskArray = newTaskArray;
}

function getDetailsCallback(response){

  details_task_description.value = response.description;
  details_task_name.innerHTML = response.title;
  details_task_date.innerHTML="Created on: "+response.creationDate;
  details_task_assignee.innerHTML="";
  try{
    details_task_assignee.innerHTML="Assigned to: "+response.assignedWorker.name+" "+response.assignedWorker.surname;
    currentTask.assignee.assigneeName = response.assignedWorker.name+" "+response.assignedWorker.surname;
    currentTask.assignee.assigneeId = parseInt(response.assignedWorker.id);
    currentTask.assignee.type = "worker";
  }catch{}
  try{
    details_task_assignee.innerHTML="Assigned to: "+response.assignedUnit.name+" (unit)";
    currentTask.assignee.assigneeName = response.assignedUnit.name;
    currentTask.assignee.assigneeId = parseInt(response.assignedUnit.id);
    currentTask.assignee.type = "unit";
  }catch{}   

  if(response.status==2){
    enableAdditionalTaskActions();
  }else{
    disableAdditionalTaskActions();
  }

  var rejectionReason = response.statusChangeReason;

  if(rejectionReason!=undefined && rejectionReason!=""){
    $(details_task_rejection_box).removeClass("d-none");
    details_task_rejection_reason.innerHTML = response.statusChangeReason;
    details_task_rejection_header.innerHTML = "Rejected on "+response.editDate+":";
  }else{
    $(details_task_rejection_box).addClass("d-none");
    details_task_rejection_reason.innerHTML = "";
    details_task_rejection_header.innerHTML = "";
  }

  disableMovingButtons(response.status);
  insertComments(response.comments);
}
//===================================================EVENTS AND REQUESTS==========================================================================
//Dynamic filters
$(tasks_search_keyword_input).on("keyup",function(){
  getFiltredTasks();
});

$(tasks_search_assignee_select).on("change",function(){
  getFiltredTasks();
});

$(tasks_search_category_select).on("change",function(){
  getFiltredTasks();
});

$(tasks_search_start_date_input).on("change",function(){
  getFiltredTasks();
});

$(tasks_search_end_date_input).on("change",function(){
  getFiltredTasks();
});

$(tasks_search_hierarchy_checkbox).on("change",function(){
  getFiltredTasks();
});

$(tasks_search_hierarchy_depth).on("keyup",function(){
  getFiltredTasks();
});

$(tasks_search_hierarchy_depth).on("change",function(){
  getFiltredTasks();
});

$(tasks_search_btn).click(function(){
  getFiltredTasks();
});

$('#statusDropdown > li').click(function(){
  var thisLink = $(this.firstChild);
  if(thisLink.hasClass("active")){
    thisLink.removeClass("active");
  }else{
    thisLink.addClass("active");
  }
  getFiltredTasks();
});
//-------------------------------------------

$(generate_report_btn).click(function(){
    var arrayPerWorker = getArrayPerWorker();
  generatePdfReport(arrayPerWorker);
});

$(tasks_show_hidden_checkbox).on("change",function(){   
  if(this.checked)      
    showAdditionalTaskColumns();
  else
    hideAdditionalTaskColumns();
});
//Detect change in reassign worker
$(re_assignee).on('change', function () {
  if(this.value!="default"){
    re_assignee_unit.disabled = true;
  }else{
    re_assignee_unit.disabled = false;
  }
});

//Detect change in reassign unit
$(re_assignee_unit).on('change', function () {
  if(this.value!="default"){
    re_assignee.disabled = true;
  }else{
    re_assignee.disabled = false;
  }
});

//Select current assignee option in modoal's select when reassign button is pressed
$(details_task_reassign_btn).click(function(){         
  selectCurrentlyAssignedWorker();
});

$(details_task_accept_btn).click(function(){ 
  var requestArray = {}
  movedTo = 3;
  requestArray["task"] = parseInt(currentTask.id);
  requestArray["status"] = movedTo; 
  performRequest("UpdateTaskStatus",requestArray,moveTaskCallbackFiltered);        
});

$(details_task_cancel_btn).click(function(){ 
  var requestArray = {}
  movedTo = 4;
  requestArray["task"] = parseInt(currentTask.id);
  requestArray["status"] = movedTo; 
  performRequest("UpdateTaskStatus",requestArray,moveTaskCallbackFiltered);        
});

$(confirm_rejection_btn).click(function(){ 
  if(reject_reason_input.value == ""){
    insertToastInformation("Rejection","To reject task you need to insert a rejection reason",5000);
    return;
  }
  var requestArray = {}
  movedTo = 1;
  requestArray["task"] = parseInt(currentTask.id);
  requestArray["status"] = movedTo;
  requestArray["reason"] = reject_reason_input.value; 
  performRequest("UpdateTaskStatus",requestArray,moveTaskCallbackFiltered);   
});

//Fetching list of tasks
$(document).ready(function() {
    isManager = false;
    var requestArray = {}
    performRequest("TaskList",requestArray,gettingTaskListCallback);
    performRequest("GetAssignables",requestArray,getAssignablesCallback);
    showHashedTask();
    fillCategories();
});

//Hash in the url is changed and task of choice is shown
$(window).on( 'hashchange', showHashedTask);

//Adding comment
$('#add_comment_btn').click(function(){         
  var requestArray = {};
  requestArray["task"] = parseInt(currentTask.id);
  requestArray["text"] = comment_textbox.value; 
  performRequest("AddComment",requestArray,addCommentCallback);
});

$(confirm_reassignment_btn).click(function(){
  var requestArray = {};
  requestArray["task"] = parseInt(currentTask.id);

  if(re_assignee.value!="default"){
    requestArray["assignedWorker"] = parseInt(re_assignee.value);
  }

  if(re_assignee_unit.value!="default"){
    requestArray["assignedUnit"] = parseInt(re_assignee_unit.value);
  }
 
  performRequest("ReassignTask",requestArray,reassignTaskCallback)
});

//Disabling checkbox with assignee when assignee to me is checked
$("#to_me_checkbox").change(function(){
  var new_value = $('#to_me_checkbox').prop('checked');
  $("#assignee_select").prop("disabled", new_value); 
  $("#assignee_select_unit").prop("disabled", new_value); 
  $("#assignee_select").val(userId);
  $("#assignee_select_unit").val("default");

  if(!new_value){
    assignee_select_unit.disabled = true;
  }
});

$(assignee_select).change(function(){
  if(this.value!="default"){
    assignee_select_unit.disabled = true;
  }else{
    assignee_select_unit.disabled = false;
  }
});

$(assignee_select_unit).change(function(){
  if(this.value!="default"){
    assignee_select.disabled = true;
  }else{
    assignee_select.disabled = false;
  }
});

//moving tasks
$('#moving_list > li').click(function(){
  var requestArray = {}
  movedTo = $( "#moving_list > li" ).index( this );
  requestArray["task"] = parseInt(currentTask.id);
  requestArray["status"] = movedTo; 
  performRequest("UpdateTaskStatus",requestArray,moveTaskCallback);
});

//add hash to url when offcanvas is shown
task_offcanvas.addEventListener('shown.bs.offcanvas', function () {
  addHashTask(currentTask.id);
})

//remove hash to url when offcanvas is hidden
task_offcanvas.addEventListener('hidden.bs.offcanvas', function () {
  removeTaskHash();
})


//getting detials about task with id==clicked_id
function getDetails(clicked_id){

  currentTask.id = clicked_id;

  var requestArray = {}
  requestArray["id"] = clicked_id;
  performRequestGet("TaskDetails",requestArray,getDetailsCallback)
};