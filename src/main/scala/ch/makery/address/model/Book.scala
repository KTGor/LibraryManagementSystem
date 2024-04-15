package ch.makery.address.model

import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}

import java.time.LocalDate
import ch.makery.address.util.Database
import ch.makery.address.util.DateUtil._
import scalikejdbc._

import scala.util.{Failure, Success, Try}

case class Book(bookNameS: String, genreS: String, authorS: String) extends Database {
  def this() = this(null, null, null)

  var bookName = new StringProperty(bookNameS)
  var volume = ObjectProperty(0)
  var genre = new StringProperty("")
  var author = new StringProperty("")
  var publisher = new StringProperty("")
  var publishDate = ObjectProperty[LocalDate](LocalDate.of(1999, 2, 21))
  var checkedOut = new StringProperty("In Library")


  def save(): Try[Int] = {
    if (!(isExist)) {
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into book (bookName, volume,
            genre, author, publisher, publishDate, checkedOut) values
            (${bookName.value}, ${volume.value}, ${genre.value},
              ${author.value},${publisher.value}, ${publishDate.value.asString}, ${checkedOut.value})
        """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
        update book
        set
        bookName = ${bookName.value},
        volume = ${volume.value},
        genre = ${genre.value},
        author = ${author.value},
        publisher = ${publisher.value},
        publishDate = ${publishDate.value.asString},
        checkedOut = ${checkedOut.value}
         where bookName = ${bookName.value}
        """.update.apply()
      })
    }

  }

  def delete(): Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
        delete from book where
          bookName = ${bookName.value}
        """.update.apply()
      })
    } else
      throw new Exception("Book not Exists in Database")
  }

  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        select * from book where
        bookName = ${bookName.value}
      """.map(rs => rs.string("bookName")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }

  }
}

object Book extends Database {
  def apply(
             bookNameS: String,
             volumeI: Int,
             genreS: String,
             authorS: String,
             publisherS: String,
             publishDateS: String,
             checkedOutS: String
           ): Book = {

    new Book(bookNameS, genreS, authorS) {
      volume.value = volumeI
      genre.value = genreS
      author.value = authorS
      publisher.value = publisherS
      publishDate.value = publishDateS.parseLocalDate
      checkedOut.value = checkedOutS
    }

  }

  def initializeTable() = {
    DB autoCommit { implicit session =>
      sql"""
      create table book (
        id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
        bookName varchar(100),
        volume int,
        genre varchar(100),
        author varchar(100),
        publisher varchar(200),
        publishDate varchar(64),
        checkedOut varchar(100)
      )
      """.execute.apply()
    }
  }

  def getAllBooks: List[Book] = {
    DB readOnly { implicit session =>
      sql"select * from book".map(rs => Book(rs.string("bookName"),
        rs.int("volume"), rs.string("genre"),
        rs.string("author"), rs.string("publisher"),
        rs.string("publishDate"), rs.string("checkedOut"))).list.apply()
    }
  }

}
