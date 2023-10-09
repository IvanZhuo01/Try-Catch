function changeProfesor(id){
    
    let profesor = document.querySelector("#profeNoCheck"+id);
   
    if(profesor.checked){
        go(config.rootUrl + '/admin/ayadirProfe/' + id, 'POST', { prof: 'true'})
        .then(d => {
            console.log("BIEN")
        })
        .catch(e => console.log('Error:',e)) 
    }
    else{
        go(config.rootUrl + '/admin/ayadirProfe/' + id, 'POST', { prof: 'false'})
        .then(d => {
            console.log("MAL")
        })
        .catch(e => console.log('Error:',e))
    }
   
}


function changeActivo(id){
    let activo = document.querySelector("#activo"+id);

    if(activo.checked){
        go(config.rootUrl + '/admin/desactivarUsuario/' + id, 'POST', { acti: 'true'})
        .then(d => {
            console.log("BIEN")
        })
        .catch(e => console.log('Error:',e)) 
    }
    else{
        go(config.rootUrl + '/admin/desactivarUsuario/' + id, 'POST', { acti: 'false'})
        .then(d => {
            console.log("BIEN")
        })
        .catch(e => console.log('Error:',e))
    }
}
console.log("alguien ha querido ser admin")
function notificacionSerAdmin(id){
    let activo = document.querySelector("#admin"+id);
    const administradores = document.querySelector("#administradores")
    let n = document.querySelector(".num-administradores");
    if(activo.checked){
        go(config.rootUrl + '/admin/serAdmin/' + id, 'POST', { admin: 'true'})
        .then(d => {
            console.log("alguien ha querido ser admin")
            // Añadimos administrador.
            administradores.insertAdjacentHTML("beforeend",`
                <div class="administrador">
                    <div class="profile-picture">
                        <img src="/user/${d.id}/pic" alt="">
                    </div>
                    <div class="info">
                        <span class="name">${d.username}</span>
                        <span class="cargo">Cargo de administrador</span>
                    </div>
                </div>
            `);

            //Actualizamos momentaneamente el numemro de aministradores.
            if(n){
                n.textContent = +n.textContent + 1;
            }
            


        })
        .catch(e => console.log('Error:',e)) 
    }
    else{
        go(config.rootUrl + '/admin/serAdmin/' + id, 'POST', { admin: 'false'})
        .then(d => {
            location.reload()
        })
        .catch(e => console.log('Error:',e))
    }
}
