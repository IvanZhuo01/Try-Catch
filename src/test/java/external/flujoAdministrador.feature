Feature: Flujo Admin

Scenario: Acceder a la pagina de administrador y buscar un usuario y hacerle profesor
    Given call read('login.feature@login_a')
    Then waitForUrl(baseUrl + '/')
    When click('#admin-link')
    Then waitForUrl(baseUrl + '/admin/')
    And input('#queryb', 'b')
    When submit().click(".search-btn")
    Then waitForUrl(baseUrl + '/admin/busquedaUsuario')
    And click('#profeNoCheck2')
    And attribute('#profeNoCheck2', 'aria-checked') == 'true'
    
    