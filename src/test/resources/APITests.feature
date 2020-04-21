Feature: Test pet website api

  Background:
    Given contract file ./petstore.contract port 9000
    And url "http://localhost:8080"

  Scenario: Fetch available dogs
    Given mock fact type = "dog"
    And mock fact name = "(string)"
    And mock fact status = "available"
    And mock path "/pets"
    And mock param type = "dog"
    And mock param status = "available"
    And mock param name = ""
    And mock method GET
    And mock status 200
    And mock response [{"name": "Archie", "type": "dog", "status": "available", "id": 10}, {"name": "Reggie", "type": "dog", "status": "available", "id": 20}]

    When path "/findFirstAvailablePet"
    And param type = "dog"
    And method GET
    Then status 200
    And match response == {"name": "ARCHIE", "id": 10}

  Scenario: Create a new pet
    Given mock fact no_pets = true
    And mock path "/pets"
    And mock request {"name": "Archie", "type": "dog", "status": "available"}
    And mock method PUT
    And mock status 200
    And mock response 12

    When path "/create-pet"
    And request {"name": "Archie", "type": "dog", "status": "available"}
    And method POST
    Then status 200
    And match response == {"status":"success", "id":12}
