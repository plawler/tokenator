package services

import com.google.inject.ImplementedBy
import com.stormpath.sdk.http.{HttpRequests, HttpMethod, HttpRequest}
import com.stormpath.sdk.oauth.AccessTokenResult
import play.api.Logger
import play.api.mvc.Request

import scala.concurrent.Future
import scala.util.Try

import scala.collection.JavaConversions._

case class Token(accessToken: String, tokenType: String, expiresIn: Int)

@ImplementedBy(classOf[StormPathTokenService])
trait TokenService {
  def retrieveToken[A](request: Request[A])(application: String): Try[Future[Token]]
  def reloadApplications(): Unit
}

class StormPathTokenService extends TokenService {

  /**
   * In Java, when asking the Application to authenticate an API authentication request, the return type of a successful authentication request will vary based on the request headers. This includes:
   *
   * 1. ApiAuthenticationResult – Authorization header is present, with the Basic method and the base64 encoded API_KEY_ID:API_KEY_SECRET.
   * 2. AccessTokenResult – HTTP Method is POST. Authorization header is present, with the Basic method and the base64 encoded API_KEY_ID:API_KEY_SECRET. As part of the query or body of the request, the ‘grant_type’ is specified as ‘client_credentials’.
   *    Content-Type is set to application/x-www-form-urlencoded.
   * 3. OauthAuthenticationResult – Authorization header is present, with the Bearer method and the OAuth 2.0 Access Token retrieved from the Stormpath SDK in a previous request.
   */

  override def retrieveToken[A](request: Request[A])(application: String): Try[Future[Token]] = {
    Try {
      val maybeApplication = StormpathClient().forApplication(application)
      maybeApplication match {
        case Some(app) =>
          val result: AccessTokenResult = app.authenticateApiRequest(toStormPathRequest(request)).asInstanceOf[AccessTokenResult]
          val token = result.getTokenResponse
          Logger.info(s"Token details: ${token.toJson}")
          Future.successful(Token(token.getAccessToken, token.getTokenType, token.getExpiresIn.toInt))
        case None => throw new IllegalArgumentException("Application could not be found")
      }
    }
  }

  override def reloadApplications(): Unit = StormpathClient().loadApps()

  private def toStormPathRequest[A](request: Request[A]): HttpRequest = {
    val headers = request.headers.toMap.mapValues(v => v.toArray)
    HttpRequests.method(HttpMethod.fromName(request.method))
      .headers(headersWithAuthorization(request))
      .queryParameters(request.rawQueryString)
      .build()
  }

  private def headersWithAuthorization[A](request: Request[A]): Map[String, Array[String]] = {
    request.getQueryString("api_key") match {
      case Some(key) => (request.headers.toMap ++ Map("Authorization" -> Seq("Bearer " + key))).mapValues(v => v.toArray)
      case None => request.headers.toMap.mapValues(v => v.toArray)
    }
  }

}