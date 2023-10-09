//Cajas de texto

let text1 = document.querySelector(".text-container .tutorial-1");
let text2 = document.querySelector(".text-container .tutorial-2");
let text3 = document.querySelector(".text-container .tutorial-3");

// Vistas

let tutorial_1 = document.getElementById("choose-university");
let tutorial_2 = document.getElementById("choose-career");
let tutorial_3 = document.getElementById("choose-level");


// Botones
let btn1 = document.querySelector(".tutorial-1-btns");
let btn2 = document.querySelector(".tutorial-2-btns");
let btn3 = document.querySelector(".tutorial-3-btns");


let sig_btn1 = document.querySelector(".tutorial-1-btns .sig-btn");

let prev_btn2 = document.querySelector(".tutorial-2-btns .prev-btn");
let sig_btn2 = document.querySelector(".tutorial-2-btns .sig-btn");


let prev_btn3 = document.querySelector(".tutorial-3-btns .prev-btn");
let sig_btn3 = document.querySelector(".tutorial-3-btns .sig-btn");

//Progress
let progress = document.querySelector(".progressbar");


//notificaciones y modales
let notification = document.querySelector(".notif-wrapper");
const curso = document.getElementById('select-curso');
const grado = document.getElementById('grado')
const totalTemplates = 3;
let etapa = 0;
let datos = {
    iduni: -1,
    grado: -1,
    curso: ""
}
// Obtener el botón de siguiente
const siguienteBtn = document.getElementById("siguienteBtn1");
let wrapper = document.querySelector(".wrapper");
// Deshabilitar el botón al principio
siguienteBtn.disabled = true;

sig_btn1.addEventListener('click', function () {
    etapa++;
    text1.style.display = "none";
    text2.style.display = "block";
    tutorial_1.style.display = "none";
    tutorial_2.style.display = "block";
    btn1.style.display = "none";
    btn2.style.display = "flex";
    progress.style.width = `${(etapa / totalTemplates) * 100}%`
    datos.iduni = document.querySelector('.card.selected').getAttribute('id');
    cargarGradosTutorial(datos.iduni)
    wrapper.style.marginTop = "150px"
});
const tarjetas = Array.from(document.querySelectorAll('.card'));

tarjetas.forEach(function (tarjeta) {
    tarjeta.addEventListener('click', function () {
        if (tarjeta.classList.contains('selected')) {
            // La tarjeta ya está seleccionada, deseleccionarla
            siguienteBtn.disabled = true;
            tarjeta.classList.remove('selected');
        } else {
            // La tarjeta no está seleccionada, seleccionarla
            // Primero, eliminar la clase "seleccionado" de todas las tarjetas
            tarjetas.forEach(function (t) {
                t.classList.remove('selected');
            });
            siguienteBtn.disabled = false;
            // Luego, agregar la clase "seleccionado" a la tarjeta seleccionada
            tarjeta.classList.add('selected');
        }
    });
});


prev_btn2.addEventListener('click', function () {
    etapa--;
    text2.style.display = "none";
    text1.style.display = "block";
    tutorial_2.style.display = "none";
    tutorial_1.style.display = "block";
    btn1.style.display = "flex";
    btn2.style.display = "none";
    progress.style.width = `${(etapa / totalTemplates) * 100}%`
    wrapper.style.marginTop = "40px"
});

sig_btn2.addEventListener('click', function () {
    etapa++;
    text2.style.display = "none";
    text3.style.display = "block";
    tutorial_2.style.display = "none";
    tutorial_3.style.display = "block";
    btn2.style.display = "none";
    btn3.style.display = "flex";
    progress.style.width = `${(etapa / totalTemplates) * 100}%`
    datos.grado = grado.value;
    imagenUniversidad();
    wrapper.style.marginTop = "150px"
});
prev_btn3.addEventListener('click', function () {
    etapa--;
    text3.style.display = "none";
    text2.style.display = "block";
    tutorial_3.style.display = "none";
    tutorial_2.style.display = "block";
    btn3.style.display = "none";
    btn2.style.display = "flex";
    progress.style.width = `${(etapa / totalTemplates) * 100}%`
});
sig_btn3.addEventListener('click', function () {
    ++etapa;
    progress.style.width = `${(etapa / totalTemplates) * 100}%`
    notification.style.display = "block";
    datos.curso = curso.value;

    go(config.rootUrl + '/user/completarTutorial/', 'POST', { datos })
        .then(response => {
            console.log('La actualización se ha realizado con éxito', response);
        })
        .catch(error => {
            console.error('Ha ocurrido un error al intentar actualizar', error);
        });

});




let titleField = document.getElementById("titulo");
let bodyField = document.getElementById("cuerpo");
const countDownTitle = document.getElementById("contador-titulo");
const maxCharact = 50;

titleField.addEventListener("input", () => {
    let gap = maxCharact - titleField.value.length;
    countDownTitle.textContent = `${gap}/${maxCharact}`;
});

//Abre el modal
let modal = document.querySelector(".modal");
let pholder = document.querySelector('#request-form div:nth-child(1) input');
document.addEventListener("click", function (event) {
    if (event.target.matches("a[href='#solicitud-modal']")) {
        event.preventDefault(); //Evitamos que cambie la url
        if (event.target.id === "university") {
            pholder.name = "universidad"
            pholder.placeholder = "Indica el nombre de la universidad";
        }
        else if (event.target.id === "career") {
            pholder.name = "career"
            pholder.placeholder = "Indíca la carrera que estás estudiando";
        }
        modal.style.display = "block";
    }
});

//Cierra el modal
window.addEventListener("click", function (event) {
    if (event.target === modal) {
        modal.style.display = "none";
        restoreInput();
    }
});

function restoreInput() {
    titleField.value = "";
    bodyField.value = "";
    countDownTitle.textContent = `${maxCharact}/${maxCharact}`;
}



function cargarGradosTutorial(uni) {

    go(`${config.rootUrl}/grados?uniId=${uni}`, "GET")
        .then((g) => {
            const selectGra = document.getElementById('grado')

            selectGra.disabled = false;
            selectGra.innerHTML = '';

            g.forEach(element => {
                console.log(element)
                const opt = document.createElement('option');
                opt.innerHTML = element.grado;
                selectGra.appendChild(opt);
            });

        }).catch(c => {
            console.log(c);
        })
}

function imagenUniversidad() {

    go(`${config.rootUrl}/imagenUniversidad?query=${datos.iduni}`, "GET")
        .then((u) => {
            ImagenTutorial(u);
        }).catch(c => {
            console.log(c);
        })
}

function searchUniversidades(event, handler) {
    const query = event.target.value;

    go(`${config.rootUrl}/searchUniversidades?query=${query}`, "GET")
        .then((u) => {
            handler(u);
        }).catch(c => {
            console.log(c);
        })
}

function tutorialUni(unis) {

    const uniContainer = document.getElementById('unisTutorial');
    siguienteBtn.disabled = true
    uniContainer.innerHTML = `
        <div class="d-flex justify-content-center align-items-center">
            <div class="spinner-border" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;

    uniContainer.innerHTML = "";

    if (unis.length === 0) {
        uniContainer.innerHTML = `
                    <div class="d-flex justify-content-center align-items-center">
                        <p>No se han encontrado resultados...</p>
                    </div>
                `;
    }

    unis.forEach(uni => {
        const imgUrl = `/user/universidad/${uni.id}/pic`;
        let newUni =
            `<div class="col-xl-4 col-lg-4 col-md-6 col-sm-6 mb-3" >
                <div class="card unis-card" id="${uni.id}">
                    <img src="${imgUrl}" class="card-img-top p-2" alt="imagen universidad">
                </div>
            </div>`
        uniContainer.insertAdjacentHTML("beforeend", newUni);
    });
    const tarjetas = Array.from(document.querySelectorAll('.card'));

    tarjetas.forEach(function (tarjeta) {
        tarjeta.addEventListener('click', function () {
            if (tarjeta.classList.contains('selected')) {
                // La tarjeta ya está seleccionada, deseleccionarla
                siguienteBtn.disabled = true;
                tarjeta.classList.remove('selected');
            } else {
                // La tarjeta no está seleccionada, seleccionarla
                // Primero, eliminar la clase "seleccionado" de todas las tarjetas
                tarjetas.forEach(function (t) {
                    t.classList.remove('selected');
                });
                siguienteBtn.disabled = false;
                // Luego, agregar la clase "seleccionado" a la tarjeta seleccionada
                tarjeta.classList.add('selected');
            }
        });
    });

}


function ImagenTutorial(uni) {
    const imgUrl = `/user/universidad/${uni.id}/pic`;
    let newUni =
        `
            <img src="${imgUrl}" alt="logo-universidad" width="300" height="100">
            <div>
                <h3 class="card-title"> ${grado.value} </h3>
            </div>
        `;
    document.getElementById('imagenuniversidad').innerHTML = newUni;
}

