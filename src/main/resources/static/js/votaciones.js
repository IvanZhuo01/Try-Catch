//TODO Funcion para votar mediante AJAX 

function voteLike(id) {
    
    go(config.rootUrl + '/preguntas/votacion/' + id, 'POST', { like: 'true'})
        .then(d => {

            let ob =JSON.stringify(d)
            const myObj = JSON.parse(ob);
            let ob2 =JSON.stringify(myObj.obj1)
            const myObj2 = JSON.parse(ob2);
            console.log(myObj2.numVotos) 

            const numVots = document.querySelector(`div[data-idres="${id}"] .panelVots h4`);
            numVots.textContent = myObj2.numVotos;

            const respuestas = document.querySelectorAll(`span[data-idUsu="${myObj2.idUsuApor}"]`);
            respuestas.forEach(f=>{
                f.textContent=myObj2.numNivelAportacion
            })

            const preguntas = document.querySelectorAll(`span[data-idUsuP="${myObj2.idUsu}"]`);
       
            preguntas.forEach(f=>{
                f.textContent=myObj2.numNivel
                console.log(myObj2.numNivel)
            })
        })
        .catch(e => {
            console.log('Error:',e)
            // window.location.href ="/login";
        }) //cambiar
}

function voteDislike(id) {
    go(config.rootUrl + '/preguntas/votacion/' + id, 'POST', { dislike: 'true' })
        .then(d => {
            let ob =JSON.stringify(d)
            const myObj = JSON.parse(ob);
            let ob2 =JSON.stringify(myObj.obj1)
            const myObj2 = JSON.parse(ob2);
            console.log(myObj2.numVotos) 

            const numVots = document.querySelector(`div[data-idres="${id}"] .panelVots h4`);
            numVots.textContent = myObj2.numVotos;

            const respuestas = document.querySelectorAll(`span[data-idUsu="${myObj2.idUsuApor}"]`);
            respuestas.forEach(f=>{
                f.textContent=myObj2.numNivelAportacion
            })

            const preguntas = document.querySelectorAll(`span[data-idUsuP="${myObj2.idUsu}"]`);
       
            preguntas.forEach(f=>{
                f.textContent=myObj2.numNivel
                console.log(myObj2.numNivel)
            })
        })
        .catch(e => {console.log('Error:',e);
        // window.location.href="/login";
        }) //Cambiar.
}



function borrarPreguntaModal(id){
    const input = document.querySelector("#borrarPreguntaModal form")

    input.action = `/preguntas/${id}/borrar`;
}


function borrarPreguntaModalAdmin(id){
    const input = document.querySelector("#borrarPreguntaModal form")

    input.action = `/admin/aportacion/${id}/borrar`;
}