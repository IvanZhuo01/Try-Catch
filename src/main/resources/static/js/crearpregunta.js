let etiquetasInput;
let etiquetasDiv;
let buttonAddon;
let etiquetaList = []

let titleField;
let etiq;
let countDownTitle;
let countDownTag;
let buttonTag;
let maxCharact = 50;
let maxTagCharact = 10;
let formCrearPreg;
let formCrearRes;
let checkEmbeb;

document.addEventListener("DOMContentLoaded", () => {
    etiquetasInput = document.getElementById("etiquetas");
    etiquetasDiv = document.getElementById("etiquetasDiv");
    buttonAddon = document.getElementById("button-addon2");

    buttonAddon.onclick = () => {
        const etiValue = etiquetasInput.value;

        if(etiValue !== "" && etiValue.length < 20){
            const etiqueta = `<span class="etiqueta badge bg-primary me-1">${etiValue}</span>`
            etiquetasDiv.insertAdjacentHTML("beforeend", etiqueta);
            etiquetasDiv.lastChild.onclick = deleteTag;
            etiquetaList.push(etiValue);

            etiquetasInput.value = "";
        }
    }

    formCrearPreg = document.querySelector("#crearform");
    formCrearRes = document.querySelector("#crearformRes");
    console.log("FORMU Preg", formCrearPreg)
    console.log("FORMU Res", formCrearRes)


    // Loop over them and prevent submission
    formCrearPreg.addEventListener('submit', validationForm, false);

    titleField = document.querySelector('#crearPregunta input[name="titulo"]');
    etiq = document.getElementById("etiquetas");
    countDownTitle= document.getElementById("contador-titulo");
    countDownTag = document.getElementById("contador-etiqueta");
    buttonTag = document.getElementById("button-addon2");

    titleField.addEventListener("input", updateTitCount);

    etiq.addEventListener("input", () => {
        etiq.value.length = 0;
        let gap =  maxTagCharact - etiq.value.length;
        countDownTag.textContent = `${gap}/${maxTagCharact}`;
    });

    buttonTag.addEventListener("click", () => {
        countDownTag.innerHTML = `${maxTagCharact}/${maxTagCharact}`
    });

    checkEmbeb = document.getElementById("embebImgdiv");
    const fileInput = document.getElementById("filedoc").onchange = (e)=>{
        checkEmbeb.classList.remove("d-none");
    }

})

function updateTitCount(){
    let gap =  maxCharact - titleField.value.length;
    countDownTitle.textContent = `${gap}/${maxCharact}`;
}


function deleteTag(event){
    console.log("ANTES", etiquetaList)

    const index = etiquetaList.indexOf(event.target.textContent);

    etiquetasDiv.removeChild(event.target)
    
    if (index > -1) { // only splice array when item is found
        etiquetaList.splice(index, 1); // 2nd parameter means remove one item only
    }
    console.log("DESPUES", etiquetaList)
}

//ARREGLAR ESTOOOOOOOo
function modoEditarPregunta(id){
    const titulo = document.querySelector('#crearPregunta input[name="titulo"]');
    const cuerpo = document.querySelector('#crearPregunta textarea[name="cuerpo"]');

    document.querySelector('#crearPregunta input[type="file"]').disabled = true;

    console.log("formCrear",formCrearPreg)

    formCrearPreg.action = `/preguntas/${id}/editar`;
    // formCrear.addEventListener('submit', validationForm, false);

    document.querySelector('#modalCrearTitle').textContent = 'Editar Pregunta'

    go(`${config.rootUrl}/preguntas/${id}/api`, "GET")
        .then((u) => {
    
            titulo.value = u.titulo;
            updateTitCount()
            cuerpo.value = u.cuerpo;
            cuerpo.innerHTML = u.cuerpo;
            
            u.etiquetas.split(',').forEach(tag => {
                const etiqueta = `<span class="etiqueta badge bg-primary me-1">${tag}</span>`
                etiquetasDiv.insertAdjacentHTML("beforeend", etiqueta);
                etiquetasDiv.lastChild.onclick = deleteTag;
                etiquetaList.push(tag);
            });

            if(u.fichero){
                checkEmbeb.classList.remove("d-none");
                if(u.embebImg){
                    document.querySelector("#embebImg").checked = true;
                }
            }

        }).catch(c => {
            console.log(c);
        })
}

function modoEditarRespuesta(id){
    const cuerpo = document.querySelector('#responder textarea[name="cuerpo"]');

    formCrearRes.action = `/preguntas/respuestas/${id}/editar`;
    // formCrear.addEventListener('submit', validationForm, false);

    document.querySelector('#responderTitle').textContent = 'Editar Respuesta'

    go(`${config.rootUrl}/preguntas/respuestas/${id}/api`, "GET")
        .then((u) => {
            cuerpo.value = u.cuerpo;
            cuerpo.innerHTML = u.cuerpo;
        }).catch(c => {
            console.log(c);
        })

}

function validationForm(event){
    console.log("PAsa validacion")

    etiquetaList.length < 1 ? etiquetasInput.setCustomValidity("Debe de haber al menos una etiqueta")
                        : etiquetasInput.setCustomValidity("");
    
    if (!formCrearPreg.checkValidity()) {
        event.preventDefault()
        event.stopPropagation()
    }else{
        formCrearPreg.etiquetas.value = etiquetaList;
    }

    formCrearPreg.classList.add('was-validated')
}