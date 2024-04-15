package ch.makery.address.view

import ch.makery.address.MainApp
import ch.makery.address.model.Account
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, TextField}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class RegisterController (
                           private val accNameField : TextField,
                           private val passwordField : TextField,
                           private val cfmPasswordField : TextField
                         ){

  var dialogStage : Stage = null
  val account = new Account("", "")

  def handleRegister(action: ActionEvent) {

    if (isInputValid()) {
      account.accName <== accNameField.text
      account.password <== passwordField.text

      MainApp.accData.removeAll(MainApp.accData)
      account.save()
      MainApp.accData ++= Account.getAllAccounts
      MainApp.showLogin()
    }
  }

  def handleBackLogin(action: ActionEvent) {
    MainApp.showLogin()
  }

  def nullCheck (x : String) = x == null || x.length == 0

  def isInputValid(): Boolean = {
    var errorMessage = ""

    if (nullCheck(accNameField.text.value)) {
      errorMessage += "No valid Account Name!\n"
    }
    if (nullCheck(passwordField.text.value)) {
      errorMessage += "No valid Password!\n"
    }
    if (nullCheck(cfmPasswordField.text.value)) {
      errorMessage += "No valid Confirm Password!\n"
    }
    if (cfmPasswordField.text.value != passwordField.text.value) {
      errorMessage += "Password and Confirm Password are Different!\n"
    }
    for(acc <- MainApp.accData){
      if(acc.accNameS == accNameField.text.value){
        errorMessage += "You already have an Existing Account!\n"
      }
    }


    if (errorMessage.length() == 0) {
      return true;
    } else {
      // Show the error message.
      val alert = new Alert(Alert.AlertType.Error) {
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()

      return false;
    }
  }
}
