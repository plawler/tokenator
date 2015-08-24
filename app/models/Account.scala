package models

import java.util.UUID

import play.api.libs.json.Json

/**
 * Created by paullawler on 8/22/15.
 */
case class Account(id: UUID, name: String, password: String)

object Account {
  implicit val accountFormat = Json.format[Account]
}
