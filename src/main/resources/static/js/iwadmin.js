

if (ws.receive) {
    const oldFn = ws.receive; // guarda referencia a manejador anterior
    ws.receive = (m) => {
        oldFn(m); // llama al manejador anterior
        console.log("iwadminnnn")
        renderNotificaciones(m);
        newNotificationInbox(m);
    }
}


function renderNotificaciones(m) {
    const modal = document.querySelector(".modal-notificaciones");
    const content = JSON.parse(m.body);
    modal.insertAdjacentHTML("beforeend", notifPendientes(content));
    modal.insertAdjacentHTML("beforeend", notifPendientesModales(content));
}

function notifPendientes(content) {
    
    return `
            <div class="notificacion notificacion-${content.id}">
                <div class="info-container" data-bs-toggle="modal" data-bs-target="#exampleModal-${content.id}">
                    <span class="topic">${content.topic}</span>
                    <span class="titulo"> ${content.titulo}</span>
                    <span class="descripcion"> ${content.cuerpo}</span>
                    <span class="emisor">Notificado por ${content.from}</span>
                </div>
                <div class="button-container">
                    <button onClick="asignar(${content.id})"><i class="bi bi-archive-fill"></i></button>
                </div>
            </div>
            
    `
}

function notifPendientesModales(content) {

    const fecha = new Date(content.sent);
    const formatedDate = fecha.getHours() + ":" + (fecha.getMinutes() > 10 ? fecha.getMinutes() : "0" + fecha.getMinutes())
    const fechaDias= (fecha.toISOString().substring(0,10)=== new Date().toISOString().substring(0,10) ? " ": fecha.toISOString().substring(0,10));
    const contexto = "modal";
    return `
    <div class="modal fade" id="exampleModal-${content.id}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">

                <div class="modal-header">
                    <button type="button" class="btn-close-${content.id}" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body">
                    <div class="notificacionFull shadow-sm rounded">
                        <span class="titulo">${content.topic}</span>
                        <span class="titulo">${content.titulo}</span>
                        <span class="descripcion">${content.cuerpo}</span>
                        <div class="emisorYfecha">
                            <span class="emisor">Notificado por <span class="nombre">${content.from}</span></span>
                            <span class="fecha">${formatedDate} </span>
                        </div>

                        <div class="botones-container">
                            <button class="volver" onClick="cierraModal(${content.id})">Volver atrás <i class="bi bi-archive-fill"></i></button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `
}


function asignar(id) {
    
    go(config.rootUrl + '/admin/' + config.userId + '/notificacion/asignar/' + id, 'POST', {})
        .then(d => {
            notificacionAsignada(id, d);
            location.reload();
        })
        .catch(e => {
            console.log('Error:', e)
        })
}

//Elminamos la notificacion de "notificaciones para gestionar" para trasladarla posteriormente al inbox
function notificacionAsignada(id, m){


    const notif = document.querySelector(`.notificacion-${id}`);
    const modalNotif = document.getElementById(`.exampleModal-${id}`)
    const parent = document.querySelector(".modal-notificaciones");
    if(parent.contains(notif)){
        parent.removeChild(notif);
    }
    else {
        console.warn("[WARNING] El item ya ha sido eliminado o ya no es accesible ")
    }
    
    if(parent.contains(modalNotif)){
        parent.remove(modalNotif);
    }
    else{
        console.warn("[WARNING] El item ya ha sido eliminado o ya no es accesible ")
    }
   


    //Aumentamos el contador ya que la notificacion tiene receptor
    let p = document.querySelector("#nav-unread");
    if (p) {
        p.textContent = +p.textContent + 1;
        newNotificationInbox(m)
    }

}

function cierraModal(id){    
    const closeButton = document.querySelector(`.btn-close-${id}`);
    closeButton.click();
}
