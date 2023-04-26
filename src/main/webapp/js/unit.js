  //====================================FUNCTIONALITY=========================================================================================
  
  // function reload(){
  //   location.reload()
  //   $("#nav-units-tab").ariaSelected = true
  // }

  function findUnit(id, obj){
    var x = true
    obj.children.every(element =>{
      if (element.id == id){
        obj = element
        x = false
      }
      return x
    })

    if (x == false)
      return obj
    else
      return {}
  }

  //====================================CALLBACKS=============================================================================================
    
  var man
  var admin
  var subunits = {};
  var workerToMove;
  var parent = {}

  function clearanceLevel(response){
    if (response.admin || response.manager)
      man = true
    if (response.admin)
      admin = true
  }

  function getUnitData(response){
      parent = response
      subunits = response.children
      // let url = new URL(window.location.href)
      // var cUrl = url
      // if (cUrl.hash == "" || cUrl.hash == "unit" + unit.id) {
      //   response = unit
      //   cUrl.hash = "unit" + response.id
      // } else {
      //   response = findUnit(cUrl.hash, unit)
      // }
      // window.location.href = cUrl
      
      //  if (response != {}){
        document.getElementById("unitName").innerHTML = response.name
        if (man)
          document.getElementById("unitName").innerHTML += '<hr><button class="btn btn-grzegorz-dark-green" type="button" id="addUnassigned" onClick="addUnassignedWorkers()">Add unassigned workers</button>'
        response.workers.forEach(element => {
          if (element.id == response.manager) {
              document.getElementById("unitLeader").innerHTML = '<li class="list-group-item rounded-4 mb-1 p-3"><div class="h5 text-center text-break">'+ element.name + " " + element.surname 
              + '</div><div class="d-flex justify-content-between mt-2"><button class="btn btn-grzegorz-dark-green" type="button" onclick="getWorkerDetails(' + element.id + ')">View</button>'
              + '</div></li>';      
          } else {
              document.getElementById("unitWorkers").innerHTML += '<li class="list-group-item rounded-4 mb-1 p-3"><div class="h5 text-center text-break">'+ element.name + " " + element.surname
              + '</div><div class="d-flex justify-content-between mt-2"><button class="btn btn-grzegorz-dark-green" type="button" onclick="getWorkerDetails(' + element.id + ')">View</button>'
              + '</div></li>'; 
              
          }
        });
        
          document.getElementById("allUnits").innerHTML += '<button class="btn btn-grzegorz-dark-green" type="button" id="createSubunit" onClick="createSubunit()">Create a subunit</button><br>'
          response.children.forEach(element => {
          recUnits(element, response.id, response.id)
          
        })
      // } else {
      //   document.getElementById("unitLeader").innerHTML = "Something went wrong."
      // }
      
      
    }

  function recUnits(arr, pId, sParent){
    var div = document.getElementById("allUnits")
    var unitDivName = "unit" + pId
    var id = "unit" + arr.id
    
    if (pId != sParent)
      div = document.getElementById(unitDivName)

    div.innerHTML += '<div id="'+ id +'"><li class="list-group-item rounded-4 mb-1 p-3"><div class="col"><div class="h5 text-center text-break">' 
    + arr.name + '</div><div class="d-flex justify-content-between mt-2"><button class="btn btn-grzegorz-dark-green" type="button" onClick="getSubunitDetails(' + arr.id + ')">View</button>'
    + '</div></li></div>'
    subunits[arr.id] = arr
      if (arr.children.length !== 0)
        arr.children.forEach(element => recUnits(element, arr.id, sParent))
  }

  function workerDetailsEdit(response){
    document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">EDIT WORKER DETAILS</div>'
    + '<div class="text-center"><div class="text-center"><hr>' + response.name + ' ' + response.surname + '<br><br>Position:<br>'
    + '<input type="text" value="' + response.position + '" name="positionEdit" id="positionEdit" class="form-control w-3"><br>'
    + '<div id="adminAccess" style="visibility: hidden"><input type="checkbox" id="dictionaryEdit" name="dictionaryEdit">'
    + '<label for="dictionary" class="col-form-label"> Dictionary access</label><br>'
    + '<input type="checkbox" id="managerEdit" name="managerEdit">'
    + '<label for="manager" class="col-form-label"> Is a manager </label><br>'
    + '<input type="checkbox" id="hrEdit" name="hrEdit">'
    + '<label for="hr" class="col-form-label"> Member of HR </label><br><br></div> '
    + '<button class="btn btn-grzegorz-dark-green" type="button" id="editWorkerButton" onClick="workerEditSend('+ response.id +')">Save</button>'
    + '</div>'
    if (admin)
      $("#adminAccess").css("visibility", "visible")
  }

  function moveWorkerGetSubunit(response){
    document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">MOVE WORKER</div>'
    + '<div class="text-center"><div class="text-center"><hr>' + response.name + ' ' + response.surname + '<br>'
    + '<select class="form-select" id="moveToSubunit" name="moveToSubunit"></select>'
    parent.children.forEach(element =>{
      document.getElementById("moveToSubunit").innerHTML += '<option value="' + element.id + '">' + element.name + '</option>'
    })
    document.getElementById("details").innerHTML += '<button class="btn btn-grzegorz-dark-green" type="button" onclick="moveWorkerContinue(' + response.id + ',' + workerToMove + ')"></button>'
    
  }


  function moveSubunitGetParent(id){
    document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">MOVE SUBUNIT</div>'
    + '<div class="text-center"><div class="text-center"><hr>' + subunits[id].name + '<br>'
    + '<select class="form-select" id="moveSubunitParent" name="moveSubunitParent"></select>'
    document.getElementById("moveSubunitParent").innerHTML += '<option value="' + parent.id + '">' + parent.name + '</option>'
    parent.children.forEach(element =>{
      console.log(element)
      if (id != element.id)
        document.getElementById("moveSubunitParent").innerHTML += '<option value="' + element.id + '">' + element.name + '</option>'
    })
    document.getElementById("details").innerHTML += '<button class="btn btn-grzegorz-dark-green" type="button" onclick="moveSubunitContinue(' + id + ')">Move</button>'

  }

  function viewWorkerDetails(response){
    if (man == true)
      document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">WORKER DETAILS<br><br><button class="btn btn-grzegorz-dark-green" type="button" onclick="editWorkerDetails(' + response.id + ')">Edit</button> <button class="btn btn-grzegorz-dark-green" type="button" onclick="moveWorker(' + response.id + ')">Move Worker</button></div>'
    else {
      document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">WORKER DETAILS</div>'
    }

    document.getElementById("details").innerHTML += '<div class="text-center"><hr>' + response.name + ' ' + response.surname + '</div><div class="text-center"><br><br>Position:<br>' + response.position + '<br><br>'
      + "This worker's permissions:<br><br>Dictionary:<br>" + response.permissions.dictionary + "<br>Manager:<br>" + response.permissions.manager + "<br>HR:<br>" + response.permissions.hr + "</div>";

  }

  function workerEditSendCallback(response){
    if(response.status=="ok"){
      alert("Permissions set!");
    }
  }

  function positionCallback(response){
    if(response.status=="ok"){
      alert("Position set!");
    }
  }

  var newMng = 0

  function setSubunitContinue(response){
    document.getElementById("details").innerHTML += '<label for="subunit">Choose a subunit:</label><br>'
    + '<select class="form-select" id="setSubunits" name="setSubunits"></select>'

    response.children.forEach(element => {
      document.getElementById("setSubunits").innerHTML += '<option value="' + element.id + '">' + element.name + '</option>'
    })

    document.getElementById("details").innerHTML += '<br><button class="btn btn-grzegorz-dark-green" type="button" onclick="setManager(newMng)">OK</button>'
    //newMng = 0
  }

  function setManagerCallback(response) {
    if (response.status == "ok")
      alert("Manager set!")
  }

  function moveWorkerCallback(response) {
    if (response.status == "ok")
      alert("Worker moved!")
  }

  function moveUnitCallback(response) {
    if (response.status == "ok")
      alert("Worker moved!")
  }

  // function createSubunitContinue(response){
    
  // }

  function createSubunit(){
    if (parent == {}){
      alert("Something went wrong")
      return
    }
    $("#details").css("visibility", "visible")
    document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">SUBUNIT CREATION</div><hr>'
    + 'Name:<br><input class="form-select" type="text" id="createSubunitName"><br>Parent unit:<br>'
    + '<select class="form-select" id="createUnitParent" name="createSubunitParent"></select>'
    document.getElementById("createUnitParent").innerHTML += '<option value="' + parent.id + '">' + parent.name + '</option>'
    parent.children.forEach(element => {
      document.getElementById("createUnitParent").innerHTML += '<option value="' + element.id + '">' + element.name + '</option>'
    })
    document.getElementById("details").innerHTML += '<button class="btn btn-grzegorz-dark-green" type="button" onclick="subunitCreation()">Create</button>'
  }

  function subunitCreationCallback(response){
    if (response.status == "ok")
      alert("Unit created!")
  }

  function addUnassignedWorkersContinue(response){
    $("#details").css("visibility", "visible")
    document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">ADD UNASSIGNED WORKERS TO UNITS</div><hr>'
    if (response.workers.length === 0)
      document.getElementById("details").innerHTML += "<center>No unassigned workers found!</center>"
    response.workers.forEach(element => {
      var id = "addUnassWorkerUnit" + element.id
      document.getElementById("details").innerHTML += '<div id="worker' + element.id + '">' + element.name + " " + element.surname 
      + '<select class="form-select" id="'+ id +'" name="createSubunitParent"></select></div><br>'
      document.getElementById(id).innerHTML += '<option value="' + parent.id + '">' + parent.name + '</option>'
      parent.children.forEach(element => {
        document.getElementById(id).innerHTML += '<option value="' + element.id + '">' + element.name + '</option>'
      })
      document.getElementById("details").innerHTML += '<button class="btn btn-grzegorz-dark-green" type="button" onclick="addUnassWorkerToUnit(' + element.id + ')">Create</button>'
  
    })
  }

  //====================================EVENTS================================================================================================

  $(document).ready(function() {
    var requestArray = {};
    performRequest("GetActions", requestArray, clearanceLevel);
    performRequest("GetHierarchy", requestArray, getUnitData); 

    $("#addUnassigned").click(function(){
      //details to visible, list the workers and an option thingy
    })

    // $("#createSubunit").click(function(){
    //   $("#details").css("visibility", "visible");
    //   var requestArray = {}
    //   performRequest("GetHierarchy", requestArray, createSubunitContinue);
    // })
  });

  function getWorkerDetails(id){
    $("#details").css("visibility", "visible");
    var requestArray = {};
    requestArray["worker"] = id;
    performRequest("WorkerDetails", requestArray, viewWorkerDetails);
  }

  function editWorkerDetails(id){
    var requestArray = {};
    requestArray["worker"] = id;
    performRequest("WorkerDetails", requestArray, workerDetailsEdit);
  }

  function moveWorker(id){
    var requestArray = {};
    requestArray["worker"] = id;
    workerToMove = id
    performRequest("WorkerDetails", requestArray, moveWorkerGetSubunit);
  }

  function workerEditSend(id){
    var requestArray = {};
    requestArray["worker"] = id; 
    requestArray["position"] = document.getElementById("positionEdit").value;
    performRequest("SetPosition", requestArray, positionCallback);
    if (document.getElementById("dictionaryEdit").checked)
      requestArray["dictionary"] = true
    else
      requestArray["dictionary"] = false

    if (document.getElementById("managerEdit").checked){
      requestArray["manager"] = true
    }
    else
      requestArray["manager"] = false

    if (document.getElementById("hrEdit").checked)
      requestArray["hr"] = true
    else
      requestArray["hr"] = false

    if (admin)
      performRequest("SetPermissions", requestArray, workerEditSendCallback);
      
      setSubunit(id)
  }

  function setSubunit(id){
    document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">SET MANAGER\'S SUBUNIT</div><hr>'
    var requestArray = {}
    newMng = id
    performRequest("GetHierarchy", requestArray, setSubunitContinue) 
  }

  function getSubunitDetails(id){
    if (subunits == {}){
      alert("Problem with loading the data. Try again")
      return
    }
    $("#details").css("visibility", "visible");
    if (man == true)
      document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">SUBUNIT DETAILS<br><br>'
      + '<button class="btn btn-grzegorz-dark-green" type="button" onclick="editSubunitDetails(' + id + ')">Edit</button>'
      + ' <button class="btn btn-grzegorz-dark-green" type="button" onclick="moveSubunitGetParent(' + id + ')">Move Subunit</button></div>'
    else {
      document.getElementById("details").innerHTML = '<div class="h5 text-center fw-semibold">SUBUNIT DETAILS</div>'
    }
    document.getElementById("details").innerHTML += '<hr><div class="text-center">' + subunits[id].name + '<br><hr>Workers:<br><br>'
    
    if (subunits[id].workers.length === 0){
      document.getElementById("details").innerHTML += "<center>No workers!</center>"
    } else {
      document.getElementById("details").innerHTML += '<ul class="list-group list-group-flush" id="subworkers"></ul>'
      subunits[id].workers.forEach(element => {
        
        document.getElementById("subworkers").innerHTML += '<div class="bg-grzegorz-grey"><li class="list-group-item rounded-4 mb-1 p-3">'
        + '<div class="h5 text-center text-break">'+ element.name + ' ' + element.surname
        + '</div><div class="d-flex justify-content-between mt-2"><button class="btn btn-grzegorz-dark-green" type="button"'
        + 'onclick="getWorkerDetails(' + element.id + ')">View</button></div></li></div>';
    })
  }

  document.getElementById("details").innerHTML += '<hr><div class="text-center">Subunits:<br></div>'
    if (subunits[id].children.length === 0){
      document.getElementById("details").innerHTML += "<center>No subunits!</center>"
    } else {
      document.getElementById("details").innerHTML += '<ul class="list-group list-group-flush" id="subsubunits"></ul>'
      subunits[id].children.forEach(element => {
        subunits[element.id] = element;
        document.getElementById("subsubunits").innerHTML += '<div class="bg-grzegorz-grey"><li class="list-group-item rounded-4 mb-1 p-3">'
        + '<div class="h5 text-center text-break">'+ element.name
        + '</div><div class="d-flex justify-content-between mt-2"><button class="btn btn-grzegorz-dark-green" type="button"'
        + 'onclick="getSubunitDetails(' + element.id + ')">View</button></div></li></div>';
      })
      
    }
    
    document.getElementById("details").innerHTML += '</div>'
  }

function setManager(id){
  var requestArray = {}
  requestArray["target"] = document.getElementById("setSubunits").value
  requestArray["worker"] = id
  performRequest("MoveWorker", requestArray, moveWorkerCallback)
  requestArray = {}
  requestArray["unit"] = document.getElementById("setSubunits").value
  requestArray["worker"] = id
  setTimeout(performRequest("SetManager", requestArray, setManagerCallback), 2000)
}

function moveWorkerContinue(target, workerid){
  var requestArray = {}
  requestArray["target"] = target
  requestArray["worker"] = workerid
  performRequest("MoveWorker", requestArray, moveWorkerCallback)
}

function moveSubunitContinue(target, unitid){
  var requestArray = {}
  requestArray["target"] = target
  requestArray["worker"] = unitid
  performRequest("MoveWorker", requestArray, moveWorkerCallback)
}

function moveSubunit(unitid, target){
  var requestArray = {}
  requestArray["target"] = target
  requestArray["unit"] = unitid
  performRequest("MoveUnit", requestArray, moveUnitCallback)
}

function subunitCreation(){
  var requestArray = {}
  requestArray["name"] = document.getElementById("createSubunitName").value
  requestArray["parent"] = document.getElementById("createUnitParent").value
  
  performRequest("CreateUnit", requestArray, subunitCreationCallback)
}

function addUnassignedWorkers(){
  var requestArray = {}
  performRequest("GetUnassignedWorkers", requestArray, addUnassignedWorkersContinue)
}

function addUnassWorkerToUnit(workerid){
  var id = "addUnassWorkerUnit" + workerid
  var requestArray = {}
  requestArray["worker"] = workerid
  requestArray["target"] = document.getElementById(id).value
  performRequest("MoveWorker", requestArray, moveWorkerCallback)
}
