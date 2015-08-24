package controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import services.{Token, StormPathClient, StormPathTokenService}

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Application @Inject() (tokenService: StormPathTokenService) extends Controller {

  implicit val tokenFormat = Json.format[Token]

  def index = Action {
    Ok("Your new application is ready.")
  }

  def token(application: String) = Action.async { implicit request =>
    tokenService.retrieveToken(request)(application) match {
      case Success(futureToken) => futureToken.map(token => Ok(Json.toJson(token)))
      case Failure(e) =>
        Logger.info(s"Token request failed. [URL: $request HEADERS: ${request.headers} REASON: ${e.getMessage}]")
        Future.successful(Unauthorized)
    }
  }

}
