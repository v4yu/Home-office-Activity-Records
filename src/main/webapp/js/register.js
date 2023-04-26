//===================================================GLOBAL VARIABLES=============================================================================
var invitation_email = "";
//===================================================HTML_ELEMENTS================================================================================
const form = document.getElementById('form');
const email = document.getElementById('email');
const password = document.getElementById('password');
const password2 = document.getElementById('password2');
const sing_up_btn = document.getElementById('singUpBtn');

//===================================================FUNCTIONS====================================================================================
form.addEventListener('submit', e => {
	e.preventDefault()

	validateInputs()
})

const setError = (element, message) => {
	const inputControl = element.parentElement
	const errorDisplay = inputControl.querySelector('.error')

	errorDisplay.innerText = message
	inputControl.classList.add('error')
	inputControl.classList.remove('success')
}

const setSuccess = element => {
	const inputControl = element.parentElement
	const errorDisplay = inputControl.querySelector('.error')

	errorDisplay.innerText = ''
	inputControl.classList.add('success')
	inputControl.classList.remove('error')
}

const isValidEmail = email => {
	const re =
		/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
	return re.test(String(email).toLowerCase())
}

const validateInputs = () => {
	// const usernameValue = username.value.trim()
	const emailValue = email.value.trim()
	const passwordValue = password.value.trim()
	const password2Value = password2.value.trim()

	// if (usernameValue === '') {
	// 	setError(username, 'Username is required')
	// } else {
	// 	setSuccess(username)
	// }

	// if (emailValue === '') {
	// 	setError(email, 'Email is required')
	// } else if (!isValidEmail(emailValue)) {
	// 	setError(email, 'Provide a valid email address')
	// } else {
	// 	setSuccess(email)
	// }

	// if (passwordValue === '') {
	// 	setError(password, 'Password is required')
	// } else if (passwordValue.length < 8) {
	// 	setError(password, 'Password must be at least 8 character.')
	// } else {
	// 	setSuccess(password)
	// }

	// if (password2Value === '') {
	// 	setError(password2, 'Please confirm your password')
	// } else if (password2Value !== passwordValue) {
	// 	setError(password2, "Passwords doesn't match")
	// } else {
	// 	setSuccess(password2)
	// }
}
//===================================================EVENTS=======================================================================================
$(document).ready(function() {
	var requestArray = {}
	performRequest("GetInvitationData",requestArray,insertInvitationEmail);
});

$("#singUpBtn").click(function(){
	var requestArray = {}

	requestArray["password"] = password.value;
	requestArray["repeated_password"] = password2.value;
	requestArray["email"] = email.value;
	if(invitation_email!=""){
		requestArray["email"]==invitation_email;
	}
	
	performRequest("RegistrationService",requestArray,RegisterServiceCallback);
})
//===================================================CALLBACKS====================================================================================
function insertInvitationEmail(response){
	if(response.email==undefined){
		invitation_email = "";
		return;
	}

	invitation_email = response.email;
	email.value = invitation_email;
	email.disabled = true;
}

function RegisterServiceCallback(response){
	if(response.status=="ok"){
		window.location = "Inactive";
	}else{
		insertToastError("Failed to register account",response.message);
	}
}