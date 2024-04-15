package ch.makery.address.view

import ch.makery.address.MainApp
import scalafxml.core.macros.sfxml
import scalafx.scene.control.{Alert, TextField}
import scalafx.stage.Stage
import scalafx.event.ActionEvent

import scala.util.control.Breaks._

@sfxml
class LoginController(
                       private val accNameField : TextField,
                       private val passwordField : TextField
                     ){



  var dialogStage : Stage = null
  var valid : Boolean = false

  def handleLogin(action : ActionEvent){
    if(isKeyValid() && valid == true){
      MainApp.showBookOverview
    }
  }

  def handleToRegister(action: ActionEvent) {
    MainApp.showRegister()
    }

  def nullChecking (x : String) = x == null || x.length == 0

  def isKeyValid() : Boolean ={
    var errorMessage = ""
    var count: Int = 0

    if (nullChecking(accNameField.text.value) && nullChecking(passwordField.text.value))
      errorMessage += "Incorrect username or password.\n"

    breakable{
      for (acc <- MainApp.accData) {
        if (accNameField.text.value != acc.accNameS || passwordField.text.value != acc.passwordS) {
          valid = false
        } else {
          errorMessage = ""
          valid = true
          break
        }
      }
    }

    if(valid == false){
      errorMessage += "Incorrect username or password.\n"
    }

    if (errorMessage.length() == 0){
      true
    } else{
      // Show the error message.
      val alert = new Alert(Alert.AlertType.Error){
        initOwner(dialogStage)
        title = "Incorrect Fields"
        headerText = "Please enter the correct Username and Password."
        contentText = errorMessage
      }.showAndWait()
      return false
    }
  }
}
