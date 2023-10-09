Feature: Flujo principal

#Cambiar a selectores con clase
#Preguntar preguntas/...

@login_a
Scenario: Entrar crear responder
    Given call read('login.feature@login_a')
    Then waitForUrl(baseUrl + '/')
    When click("#ultPreg")
    Then waitForUrl(baseUrl + '/actividad')
    When click(".boton-flotante span")
    And delay(1000)
    And input('#titulo', 'titulo test')
    And input('#cuerpo', 'cuerpo test')
    And input('#etiquetas', 'Test')
    When click('#button-addon2')
    When submit().click("#continuarButton")
    And delay(1000)
    Then waitForUrl(baseUrl + '/preguntas')
    When submit().click("#logout") 

    # usuario b responder a esa pregunta
    Given call read('login.feature@login_b')
    And delay(1000)
    Given driver baseUrl + '/'
    And delay(1000)
    And input('#queryb', 'titulo test')
    When submit().click(".search-btn")
    Then waitForUrl(baseUrl + '/busqueda')
    And delay(1000)
    When click('.pregunta-card a')
    And delay(1000)
    When click('#responderButton')
    And delay(1000)
    And input('#cuerpo', 'cuerpo respuesta test')
    When submit().click("#continuarButtonRes")
    Then waitForUrl(baseUrl + '/preguntas')
    When submit().click("#logout") 

    #usuario a vota esa respuesta
    Given call read('login.feature@login_a')
    Then waitForUrl(baseUrl + '/')
    And input('#queryb', 'titulo test')
    When submit().click(".search-btn")
    Then waitForUrl(baseUrl + '/busqueda')
    When click('.pregunta-card a')
    And delay(1000)
    Then waitForUrl(baseUrl + '/preguntas')
    When click('.res button')
    Then waitForUrl(baseUrl + '/preguntas')
    And delay(1000)