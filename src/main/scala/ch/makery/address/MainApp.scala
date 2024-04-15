package ch.makery.address
import ch.makery.address.model.{Account, Book}
import ch.makery.address.util.Database
import ch.makery.address.view.{BookEditDialogController, LoginController, RegisterController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.Image
import scalafx.stage.Stage
import scalafx.stage.Modality

object MainApp extends JFXApp {

  //Initialize database
  Database.setupDB()

  /**
   * The data as an observable list of Books.
   */
  val bookData = new ObservableBuffer[Book]()
  bookData ++= Book.getAllBooks

  val accData = new ObservableBuffer[Account]()
  accData ++= Account.getAllAccounts

  // transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  // initialize the loader object.
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  // Load root layout from fxml file.
  loader.load();
  // retrieve the root component BorderPane from the FXML
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  // initialize stage
  stage = new PrimaryStage {
    title = "Library Management System"
    scene = new Scene {
      stylesheets += getClass.getResource("view/DarkTheme.css").toString()
      root = roots
    }
    icons += new Image(getClass.getResourceAsStream("/images/library_icon.jpg"))
  }
  stage.setResizable(false)

  // actions for display book overview window
  def showBookOverview() = {
    val resource = getClass.getResource("view/BookOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showBookEditDialog(book: Book): Boolean = {
    val resource = getClass.getResourceAsStream("view/BookEditDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[BookEditDialogController#Controller]

    val dialog = new Stage() {
      title = "Library Management System"
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      scene = new Scene {
        stylesheets += getClass.getResource("view/DarkTheme.css").toString()
        root = roots2
      }
      icons += new Image(getClass.getResourceAsStream("/images/library_icon.jpg"))
    }
    dialog.setResizable(false)
    control.dialogStage = dialog
    control.book = book
    dialog.showAndWait()
    control.okClicked
  }

  def showLogin() = {
    val resource = getClass.getResource("view/Login.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }


  def showRegister() = {
    val resource = getClass.getResource("view/Register.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  // call to display Login page when app start
  showLogin()
}