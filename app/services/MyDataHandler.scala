package services


import models.Account

import com.fasterxml.uuid.Generators
import play.api.libs.concurrent.Execution.Implicits._
import org.joda.time.DateTime
import sun.misc.BASE64Encoder

import scala.concurrent.Future
import scalaoauth2.provider.{ClientCredential, AuthInfo, AccessToken, DataHandler}

/**
 * Created by paullawler on 8/22/15.
 */
class MyDataHandler extends DataHandler[Account] {

  override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] =
    Future.successful(true)

  override def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[Account]] =
    Future.successful(Some(Account(Generators.timeBasedGenerator().generate(), "Bob Client", "p@55w3rd")))

  override def createAccessToken(authInfo: AuthInfo[Account]): Future[AccessToken] =
    Future.successful {
      AccessToken(TokenThing.generate, Some(TokenThing.generate), None, Some(30), DateTime.now().toDate)
    }

  override def refreshAccessToken(authInfo: AuthInfo[Account], refreshToken: String): Future[AccessToken] =
    createAccessToken(authInfo)

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[Account]]] =
    Future.successful(None)

  override def getStoredAccessToken(authInfo: AuthInfo[Account]): Future[Option[AccessToken]] =
    createAccessToken(authInfo).flatMap(token => Future.successful(Some(token)))

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[Account]]] = ???

  override def findUser(username: String, password: String): Future[Option[Account]] = ???

  override def deleteAuthCode(code: String): Future[Unit] = ???

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[Account]]] = ???

  override def findAccessToken(token: String): Future[Option[AccessToken]] = Future.successful(None)
}

object TokenThing {
  def generate = {
    val id = Generators.randomBasedGenerator().generate().toString
    new BASE64Encoder().encode(id.getBytes)
  }
}

