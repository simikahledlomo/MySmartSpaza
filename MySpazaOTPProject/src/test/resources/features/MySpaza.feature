Feature: Complete Business Registration for Spaza Shop

  @smoke @registration
  Scenario Outline: Successful completion of the full business registration journey
    Given the user navigates to the registration page
    When the user enters full name "<name>"
    And the user enters email address "<email>"
    And the user enters password "<password>"
    And the user confirms password "<password>"
    And the user clicks the submit button

    Examples:
      | name               		| email              												| password   |
      | Simikahle Dlomo    		| simikahledlomo+21@gmail.com       				| P@ssw0rd1  |
     
      

 # @registration @negative
  #Scenario: Registration with invalid email format
   # Given the user navigates to the registration page
    #When the user enters full name "Test User"
 #   And the user enters email address "invalid-email"
  #  And the user enters password "P@ssw0rd1"
   # And the user confirms password "P@ssw0rd1"
    #And the user clicks the submit button
    #Then an error message should be displayed