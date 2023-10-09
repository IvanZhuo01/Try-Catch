

if (ws.receive) {
    const oldFn = ws.receive; // guarda referencia a manejador anterior

    ws.receive = (m) => {
        console.log("MELLEGA NOTIII")
        oldFn(m);
        showTooltip(m);
        newNotificationInbox(m);
    }
}

const toastTrigger = document.getElementById('liveToastBtn')
const toastLiveExample = document.getElementById('liveToast')

function showTooltip(mensaje) {
    const content = JSON.parse(mensaje.body);
    const toastLiveExample = document.getElementById('liveToast')

    const toast = new bootstrap.Toast(toastLiveExample)
    toastLiveExample.innerHTML = renderToolTip(content);
    toast.show()
    
    let titulo = `${content.titulo}`;
    if(titulo.includes("Descenso") || titulo.includes("Ascenso")){
        document.querySelector(".logoutButton").click()
    }



}

function newNotificationInbox(mensaje) {

    const content = mensaje.hasOwnProperty("body") ? JSON.parse(mensaje.body) : mensaje;
    if (content.to !== "sin receptor") {
        const inbox = document.getElementById("notiInbox");



        console.log(document.getElementById("nav-unread").textContent, (document.getElementById("nav-unread").textContent) === "1", inbox.lastChild)

        if ((document.getElementById("nav-unread").textContent) === "1") {
            document.querySelector("#emptyInbox").classList.add("d-none")
        }

        inbox.insertAdjacentHTML("beforeend", renderInboxNotification(content))
    }
}

//Renderiza el toast con todos sus campos
function renderToolTip(m) {
    let titulo = "";
    if (m.topic === "") {
        titulo = `${m.titulo}`
    }
    else {
        titulo = `[${m.topic}] ${m.titulo}`
    }
    const fecha = new Date(m.sent);
    const formatedDate = fecha.getHours() + ":" + (fecha.getMinutes() > 10 ? fecha.getMinutes() : "0" + fecha.getMinutes())
    const fechaDias= (fecha.toISOString().substring(0,10)=== new Date().toISOString().substring(0,10) ? " ": fecha.toISOString().substring(0,10));
    return `
    <div class="toast-header">
      <h4 class="m-1"><i class="bi bi-bell-fill"></i></h4>
      <strong class="me-auto">${titulo}</strong>
      <small>${formatedDate} <span> ${fechaDias}<\span></small>
      <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
    <div class="toast-body">
        ${m.cuerpo}
    </div>
    <div class="toast-bar ${m.tipo}"></div>
    </div>
    `
}

function renderInboxNotification(m) {

    const fecha = new Date(m.sent);
    const formatedDate = fecha.getHours() + ":" + (fecha.getMinutes() > 10 ? fecha.getMinutes() : "0" + fecha.getMinutes())
    const fechaDias= (fecha.toISOString().substring(0,10)=== new Date().toISOString().substring(0,10) ? " ": fecha.toISOString().substring(0,10));
    return `
        <div class="card m-2" data-notiId="${m.id}">
        
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <div class="card-text" onclick="mostrarNotificacionModal(${m.receiverId}, ${m.id})">
                        <h6 class="notiHeader"><i class="bi bi-info-circle"></i>  ${m.titulo}</h6>
                        <p class="notiCuerpo">${m.cuerpo}</p>
                        <p class="notiCuerpo noti-date m-0">${formatedDate} <span> ${fechaDias}<\span></p>
                    </div>
                    <div>
                        <a onclick="setNotificationRead(${m.receiverId}, ${m.id})"><button class="check-button text-primary"><i class="bi bi-check-square-fill check-icon"></i></button></a>
                    </div>
                </div>
                
               
            </div>
        
        </div>
    `
}

function setNotificationRead(userId, id) {
    go(config.rootUrl + '/user/' + userId + '/notificacion/' + id, 'POST', {})
        .then(d => {
            console.log(d)
            clearNotificationFromImbox(id);
        })
        .catch(e => {
            console.log('Error:', e)
        }) //cambiar
}

function clearNotificationFromImbox(id) {
    const noti = document.querySelector("[data-notiid='" + id + "']");
    document.getElementById("notiInbox").removeChild(noti);
    let p = document.querySelector("#nav-unread");
    p.textContent = parseInt(p.textContent) - 1;

    if (p.textContent === '0') {
        document.querySelector("#emptyInbox").classList.remove("d-none")
    }
}

function mostrarNotificacionModal(userId, id) {
    const modal = document.querySelector("#visualizarNoti")

    go(config.rootUrl + '/user/' + userId + '/notificacion/' + id, 'GET')
        .then(m => {
            const fecha = new Date(m.sent);
            const formatedDate = fecha.getHours() + ":" + (fecha.getMinutes() > 10 ? fecha.getMinutes() : "0" + fecha.getMinutes())
            const fechaDias= (fecha.toISOString().substring(0,10)=== new Date().toISOString().substring(0,10) ? " ": fecha.toISOString().substring(0,10));
            modal.innerHTML = `
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <div class="d-flex justify-content-between align-items-center w-100">
                            <h1 class="modal-title fs-5" id="visualizarNotiLabel"><i class="bi bi-bell-fill"></i>  ${m.titulo}</h1>
                           
                        </div>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        ${m.cuerpo}
                    </div>
                    
                    <div class="modal-body">
                        <p class="notiCuerpo"> ${m.from === "sin emisor" ? "" : "Notificado por @" + m.from}</p>
                        <span> ${fechaDias}<\span> <small>${formatedDate} </small>
                    </div>
                </div>
            </div>`

            new bootstrap.Modal(document.querySelector("#visualizarNoti")).show();
        })
        .catch(e => {
            console.log('Error:', e)
        }) //cambiar

}