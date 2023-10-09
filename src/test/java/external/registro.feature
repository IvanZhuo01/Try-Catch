
Feature: registro

Scenario: registro correcto
    Given driver baseUrl + '/login'
    When click("#sign a")
    And input('#name', 'pablo')
    And input('#username', 'pablo@ucm.es')
    And input('#password', 'aa')
    And input('#confirmacion', 'aa')
    When submit().click("#buttonSubmit")
    Then waitForUrl(baseUrl + '/')

Scenario: registro incorrecto
    Given driver baseUrl + '/login'
    When click("#sign a")
    And input('#name', 'hola')
    And input('#username', 'hola@ucm.es')
    And input('#password', 'aa')
    And input('#confirmacion', 'asd')
    When submit().click("#buttonSubmit")
    Then waitForUrl(baseUrl + '/register')
