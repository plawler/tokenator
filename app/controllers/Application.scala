package controllers

import services.{Token, StormPathTokenService}

import play.api.libs.json.Json
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import com.google.inject.Inject
import org.jscience.physics.amount.Amount
import org.jscience.physics.model.RelativisticModel
import javax.measure.unit.SI.KILOGRAM
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Application @Inject() (tokenService: StormPathTokenService) extends Controller {

  implicit val tokenFormat = Json.format[Token]

  def index = Action {
    Ok("Your new application is ready.")
  }

  def science = Action {
    RelativisticModel.select()
    val m = Amount.valueOf("12 GeV").to(KILOGRAM)
    val testRelativity = s"E=mc^2: 12 GeV = $m"
    Ok(testRelativity)
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
