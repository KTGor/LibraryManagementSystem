package ch.makery.address.util

import scalikejdbc._
import ch.makery.address.model.{Account, Book}

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:libraryDB;create=true;";
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "me", "mine")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession


}

object Database extends Database {
  def setupDB() = {
    if (!hasDBInitialize) {
      Book.initializeTable()
    }

    if (!hasDBInitialize) {
      Account.initializeTable()
    }
  }

  def hasDBInitialize: Boolean = {

    DB getTable "Book" match {
      case Some(x) => true
      case None => false
    }

    DB getTable "Account" match {
      case Some(x) => true
      case None => false
    }
  }
}
