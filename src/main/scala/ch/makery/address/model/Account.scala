package ch.makery.address.model

import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}

import java.time.LocalDate
import ch.makery.address.util.Database
import ch.makery.address.util.DateUtil._
import scalikejdbc._

import scala.util.{Failure, Success, Try}

case class Account(accNameS: String, passwordS: String) extends Database {
  def this() = this(null, null)

  var accName = new StringProperty(accNameS)
  var password = new StringProperty(passwordS)

  def save(): Try[Int] = {
    if (!(isExist)) {
      Try(DB autoCommit { implicit session =>
        sql"""
        insert into account (accName, password) values
          (${accName.value}, ${password.value})
      """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
      update account
      set
      accName = ${accName.value},
      password = ${password.value}
       where accName = ${accName.value}
      """.update.apply()
      })
    }

  }


  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        select * from account where
        accName = ${accName.value}
      """.map(rs => rs.string("accName")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }

  }
}

object Account extends Database {
  def apply(
             accNameS: String,
             passwordS: String
           ): Account = {

    new Account(accNameS, passwordS) {
      accName.value = accNameS
      password.value = passwordS
    }
  }

  def initializeTable() = {
    DB autoCommit { implicit session =>
      sql"""
      create table account (
        id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
        accName varchar(100),
        password varchar(100)
      )
      """.execute.apply()
    }
  }

  def getAllAccounts: List[Account] = {
    DB readOnly { implicit session =>
      sql"select * from account".map(rs => Account(rs.string("accName"),
        rs.string("password"))).list.apply()
    }
  }

}
