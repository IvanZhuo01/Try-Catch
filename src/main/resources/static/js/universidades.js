

function searchUniversidades(event, handler) {
    const query = event.target.value;

    go(`${config.rootUrl}/searchUniversidades?query=${query}`, "GET")
        .then((u) => {
            handler(u);
        }).catch(c => {
            console.log(c);
        })
}



function createContent(unis) {
    const uniContainer = document.getElementById('unisContainer');

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
        console.log(uni)
        console.log(uni.index);
        const imgUrl = `/user/universidad/${uni.id}/pic`;
        let newUni = `
                    
                    <div class="col-md-4 mb-3">
                        <div class="card university-card">
                            <img src="${imgUrl}" class="card-img-top p-2" alt="imagen universidad" style="height: 40%;">
                            <div class="card-body">
                                <h5 class="card-title"><a href="${window.location.pathname === '/universidades' ? "/universidades/" + uni.id : "/admin/universidades/" + uni.id}">${uni.nombre}</a></h5>
                                <div class="d-flex">
                                    <p><i class="bi bi-geo-alt-fill"></i><p class="university-data card-text m-0 text-primary">${uni.localizacion}</p></p>
                                </div>
                                <p class="university-data">${uni.descripcion}</p>
                            </div>
                        </div>
                    </div>
                `
        uniContainer.insertAdjacentHTML("beforeend", newUni);
    });

}


