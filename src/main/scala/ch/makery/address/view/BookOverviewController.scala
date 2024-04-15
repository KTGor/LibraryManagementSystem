package ch.makery.address.view

import ch.makery.address.model.{Account, Book}
import ch.makery.address.MainApp
import scalafx.scene.control.{Alert, ChoiceBox, Label, TableColumn, TableView, TextField}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import javafx.collections.transformation.{FilteredList, SortedList}
import javafx.collections.FXCollections


@sfxml
class BookOverviewController(
                              private val bookTable: TableView[Book],

                              private val bookNameColumn: TableColumn[Book, String],

                              private val volumeColumn: TableColumn[Book, Int],

                              private val genreColumn: TableColumn[Book, String],

                              private val authorColumn: TableColumn[Book, String],

                              private val bookNameLabel: Label,

                              private val volumeLabel: Label,

                              private val genreLabel: Label,

                              private val authorLabel: Label,

                              private val publisherLabel: Label,

                              private val publishDateLabel: Label,

                              private val checkedOutLabel: Label,

                              private val searchField : TextField,

                              private val searchChoice : ChoiceBox[String]

) {

  bookTable.items = MainApp.bookData
  // initialize columns's cell values
  bookNameColumn.cellValueFactory = {
    _.value.bookName
  }
  volumeColumn.cellValueFactory = {
    _.value.volume
  }
  genreColumn.cellValueFactory = {
    _.value.genre
  }
  authorColumn.cellValueFactory = {
    _.value.author
  }

  // initialize Table View display contents model


  private def showBookDetails(book: Option[Book]) = {
    import ch.makery.address.util.DateUtil._
    book match {
      case Some(book) =>
        // Fill the labels with info from the book object.
        bookNameLabel.text <== book.bookName
        volumeLabel.text = book.volume.value.toString
        genreLabel.text <== book.genre
        authorLabel.text <== book.author
        publisherLabel.text <== book.publisher
        publishDateLabel.text = book.publishDate().asString
        checkedOutLabel.text <== book.checkedOut

      case None =>
        // Book is null, remove all the text.
        bookNameLabel.text.unbind()
        volumeLabel.text.unbind()
        genreLabel.text.unbind()
        authorLabel.text.unbind()
        publisherLabel.text.unbind()
        publishDateLabel.text.unbind()
        checkedOutLabel.text.unbind()

        bookNameLabel.text = ""
        volumeLabel.text = ""
        genreLabel.text = ""
        authorLabel.text = ""
        publisherLabel.text = ""
        publishDateLabel.text = ""
        checkedOutLabel.text = ""
    }
  }
  showBookDetails(None)

  bookTable.selectionModel().selectedItem.onChange(
    (x, y, z) => showBookDetails(Option(z))
  )

  /**
   * Called when the user clicks on the delete button.
   */
  def handleDeleteBook(action: ActionEvent) = {
    search()
    val selectedIndex = bookTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) {
      MainApp.bookData.remove(selectedIndex).delete()
    } else {
      // Nothing selected.
      val alert = new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Book Selected"
        contentText = "Please select a book in the table."
      }.showAndWait()
    }
  }

  def handleNewBook(action: ActionEvent) = {
    val book = new Book("", "", "")
    val okClicked = MainApp.showBookEditDialog(book);
    if (okClicked) {
      MainApp.bookData.removeAll(MainApp.bookData)
      book.save()
      MainApp.bookData ++= Book.getAllBooks
    }
  }

  def handleEditBook(action: ActionEvent) = {
    val selectedBook = bookTable.selectionModel().selectedItem.value
    if (selectedBook != null) {
      val okClicked = MainApp.showBookEditDialog(selectedBook)

      if (okClicked) {
        showBookDetails(Some(selectedBook))
        selectedBook.save()
        MainApp.bookData.removeAll(MainApp.bookData)
        MainApp.bookData ++= Book.getAllBooks
      }

    } else {
      // Nothing selected.
      val alert = new Alert(Alert.AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Book Selected"
        contentText = "Please select a book in the table."
      }.showAndWait()
    }
  }

  private def search() = {

    val list = FXCollections.observableArrayList[String]
    list.addAll("by Book Name", "by Genre", "by Author")
    searchChoice.setItems(list)
    searchChoice.setValue(list.get(0))

    val filtered = new FilteredList[Book](MainApp.bookData, (b: Book) => true)

    searchField.textProperty.addListener((observable, oldValue, newValue) => {
      filtered.setPredicate(Book => {
        // If filter text is empty, display all books.
        if (newValue == null || newValue.isEmpty) {
          true
        }
          val lowerCaseFilter = newValue.toLowerCase()
          searchChoice.getValue match {
            case "by Book Name" =>
              if (Book.bookNameS.toLowerCase().contains(lowerCaseFilter)) {
                true
              }else{
                false
              }
            case "by Genre" =>
              if (Book.genreS.toLowerCase().contains(lowerCaseFilter)) {
                true
              } else {
                false
              }
            case "by Author" =>
              if (Book.authorS.toLowerCase().contains(lowerCaseFilter)) {
                true
              } else {
                false
              }
          }
      })
    })
    // filtered into sorted
    val sorted = new SortedList[Book](filtered)

    //bind the sorted comparator to the tableview comparator
    sorted.comparatorProperty.bind(bookTable.comparatorProperty)

    //add sorted to the table
    bookTable.setItems(sorted)
  }
  search()

  def handleClear(action: ActionEvent) = {
    searchField.clear()
  }

  def handleLogOut(action: ActionEvent) = {
    MainApp.showLogin()
  }

}
