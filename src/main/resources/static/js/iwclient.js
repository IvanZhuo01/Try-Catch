
const modalButton = document.getElementById("submitSolicitud");
const inputText = document.getElementById('titulo');

//
modalButton.addEventListener("click", function (event) {
    event.preventDefault();
    let url = "[WS CHANNEL]"
    if (inputText.name === "universidad") {
        url = '/user/solicitudUni';
    }
    else if (inputText.name === "career") {
        url = '/user/solicitudTitulacion';
    }

    const title = document.getElementById("titulo");
    const details = document.getElementById("cuerpo");
    let universidad = title.value;
    let detalles = details.value;

    let json = { titulo: universidad, cuerpo: detalles };

    go(config.rootUrl + `${url}`, 'POST', json)
        .then(d => {
            console.log("Se ha enviado correctamente la solicitud")
        })
        .catch(e => {
            console.log('Error:', e)
        })

});

