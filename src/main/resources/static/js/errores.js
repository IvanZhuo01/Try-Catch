function mostrarNotificaciones(errors) {
    
   arrayErrores=errors.split(",");
  
   document.querySelector('#crearPreguntaError').textContent=errors;
   document.getElementsByClassName('boton-flotante')[0].click();
}

