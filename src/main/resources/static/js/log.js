
// Variables modificadoras
// let input = document.getElementById('');
let signButton = document.getElementById('sign');
let title = document.getElementById('input-title');
let nameField = document.getElementById('name-field');
let confirmPassword = document.getElementById('confirm-password');
let form = document.getElementById("form");

let mailHolder = document.querySelector(".input-field #username");
let nameHolder = document.querySelector(".input-field #name");
//Variable que representa si el usuario tiene cuenta.
let createAccount = true;

signButton.addEventListener('click', function () {
    document.querySelector("#errorBox").classList.add("d-none")
    
    //Registro
    if (createAccount) {
        title.innerHTML = "¡Bienvenido!, Registrate en T&C"
        nameField.style.height = "65px";
        mailHolder.placeholder = "Dirección de email (25 letras max)";
        nameHolder.placeholder = "Nombre de usuario (25 letras max)";
        confirmPassword.style.height = "65px";
        signButton.innerHTML = "<span id='sign'> ¿Ya estás registrado? <a> Inicia sesión </a> </span>"
        form.setAttribute("action", "/register");

    }
    //Signin
    else {
        title.innerHTML = "Inicio de sesión";
        nameField.style.height = "0px";
        mailHolder.placeholder = "Dirección de email";
        nameHolder.placeholder = "Nombre de usuario";
        confirmPassword.style.height = "0px";
        signButton.innerHTML = "<span id='sign'> ¿Eres un nuevo usuario? <a> Crea una cuenta </a> </span>"
        form.setAttribute("action", "/login");

    }
    createAccount = !createAccount;
});

document.addEventListener("DOMContentLoaded", () => {
    if(location.search.startsWith('?error')){
        document.querySelector("#errorBox").classList.remove("d-none")
    }

    // add your after-page-loaded JS code here; or even better, call 
    // 	 document.addEventListener("DOMContentLoaded", () => { /* your-code-here */ });
    //   (assuming you do not care about order-of-execution, all such handlers will be called correctly)
});


