

let datos = document.querySelector('.datos-perfil');
let actividad = document.getElementById('qa-wrapper');


function changeUni(e){
    const uni = e.target.value;
    cargarGrados(uni);
}

function cargarGrados(uni){
    //const url = `${config.rootUrl}/grados`;
    go(`${config.rootUrl}/grados?uniId=${uni}`, "GET")
        .then((g)=>{
            const selectGra = document.getElementById('grado')
            document.getElementById('curso').disabled = false;

            selectGra.disabled = false;
            selectGra.innerHTML='';

            g.forEach(element => {
                console.log(element)
                const opt = document.createElement('option');
                opt.value = element.id;
                opt.innerHTML = element.grado;
                selectGra.appendChild(opt);
            });
            
        }).catch(c => {
            console.log(c);
        })
}


function cargaActividad(){
  console.log("actividad")
  actividad.style.display = "block";
  datos.style.display = "none";
}

function cargaDatos(){
  console.log("datos")
  actividad.style.display = "none";
  datos.style.display = "block";
}

setTimeout(function() {
    const elementos = document.querySelectorAll('.fade-in');
    elementos.forEach(function(elemento) {
      elemento.style.opacity = 1;
      (function fade() {
        if ((elemento.style.opacity -= 0.1) < 0) {
          elemento.style.display = "none";
        } else {
          requestAnimationFrame(fade);
        }
      })();
    });
  }, 3000);
  