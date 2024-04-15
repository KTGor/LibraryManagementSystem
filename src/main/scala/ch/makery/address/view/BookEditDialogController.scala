package ch.makery.address.view

import ch.makery.address.model.Book
import scalafx.scene.control.{TextField, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import ch.makery.address.util.DateUtil._
import scalafx.event.ActionEvent

@sfxml
class BookEditDialogController(

   private val bookNameField : TextField,
   private val volumeField : TextField,
   private val genreField : TextField,
   private val authorField : TextField,
   private val publisherField : TextField,
   private val publishDateField : TextField,
   private val checkedOutLabel: Label

                                 ){
  var         dialogStage : Stage  = null
  private var _book    : Book = null
  var         okClicked            = false

  def book = _book
  def book_=(x : Book) {
    _book = x

    bookNameField.text = _book.bookName.value
    volumeField.text  = _book.volume.value.toString
    genreField.text    = _book.genre.value
    authorField.text = _book.author.value
    publisherField.text      = _book.publisher.value
    publishDateField.text  = _book.publishDate.value.asString
    publishDateField.setPromptText("dd.mm.yyyy")
    checkedOutLabel.text      = _book.checkedOut.value
  }

  def handleOk(action :ActionEvent){

    if (isInputValid()) {
      _book.bookName <== bookNameField.text
      _book.volume.value = volumeField.getText().toInt
      _book.genre <== genreField.text
      _book.author <== authorField.text
      _book.publisher <== publisherField.text
      _book.publishDate.value = publishDateField.text.value.parseLocalDate
      _book.checkedOut <== checkedOutLabel.text

      okClicked = true;
      dialogStage.close()
    }
  }

  def handleCheck(action :ActionEvent){
    if(_book.checkedOut.value == "In Library"){
      checkedOutLabel.text = "Checked Out"
      _book.checkedOut.value = "Checked Out"
    }else if(_book.checkedOut.value == "Checked Out"){
      checkedOutLabel.text = "In Library"
      _book.checkedOut.value = "In Library"
    }
  }

  def handleCancel(action :ActionEvent) {
    dialogStage.close();
  }

  def nullChecking (x : String) = x == null || x.length == 0

  def isInputValid() : Boolean = {
    var errorMessage = ""

    if (nullChecking(bookNameField.text.value))
      errorMessage += "No valid book name!\n"
    if (nullChecking(volumeField.text.value))
      errorMessage += "No valid volume!\n"
    else {
      try {
        Integer.parseInt(volumeField.getText());
      } catch {
        case e: NumberFormatException =>
          errorMessage += "No valid volume (must be an integer)!\n"
      }
    }
    if (nullChecking(genreField.text.value))
      errorMessage += "No valid genre!\n"
    if (nullChecking(authorField.text.value))
      errorMessage += "No valid author!\n"
    if (nullChecking(publisherField.text.value))
      errorMessage += "No valid publisher!\n"
    if (nullChecking(publishDateField.text.value))
      errorMessage += "No valid publish date!\n"
    else {
      if (!publishDateField.text.value.isValid) {
        errorMessage += "No valid publish date. Use the format dd/mm/yyyy!\n";
      }
    }

    if (errorMessage.length() == 0) {
      return true;
    } else {
      // Show the error message.
      val alert = new Alert(Alert.AlertType.Error){
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()

      return false;
    }
  }
}
