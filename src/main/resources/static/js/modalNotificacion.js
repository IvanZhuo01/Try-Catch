function notificacionSerprofe(){
   let titulo= document.querySelector("#titulo");
   titulo.textContent="Solicitud para ser profesor";
   let crearformNot= document.querySelector("#crearformNot");
   crearformNot.setAttribute("action", "/user/solicitarProfesor");
   
}

function notificacionReportarUsu(idreportado){
    let titulo= document.querySelector("#titulo");
    titulo.textContent="Solicitud para reportar usuario";
    let crearformNot= document.querySelector("#crearformNot");
    crearformNot.setAttribute("action", "/user/reporte");
    crearformNot.insertAdjacentHTML("beforeend", `<input type="text" name="reportado" id="reportado" value="${idreportado}" hidden>`)
    
}

function notificacionReporteAdinAUsu(id){
    let titulo= document.querySelector("#titulo");
    titulo.textContent="Aviso de reporte";
    let crearformNot= document.querySelector("#crearformNot");
    crearformNot.setAttribute("action", "/admin/reporte/"+ id);
}