Feature: ser profesor

Scenario: Solicitud profesor

    # inicia sesion con cuenta de usuario 
    Given call read('login.feature@login_b')
    And delay(1000)
    Given driver baseUrl + '/'
    And delay(1000)
    And click('.vistaPerfl')

    # En la vista perfil 
    Given driver baseUrl + '/perfil/2'
    And click('.serProfe')
    And input('#cuerpo', 'Soy profesor en la UCM, mi id es 123456A')
    When submit().click("#continuarButton")
    And delay(1000)
    When submit().click("#logout") 

    #Inicia sesion con cuenta de amdministrador y asciende de rol a usuario b 
    Given call read('flujoAdministrador.feature')
    And click('.vistaPrincipal')
    When submit().click("#logout") 

    #Vuelve a iniciar sesion con el usuario b y comprobamos si el usuario es o no profesor en funcion de si está o no el boton de ascender de rol
    Given call read('login.feature@login_b')
    And delay(1000)
    Given driver baseUrl + '/'
    And delay(1000)
    And click('.vistaPerfl')
    Given driver baseUrl + '/perfil/2'
    Then def msg = script("document.querySelector('.serProfe') == null")
    Then match msg == true



   

    

