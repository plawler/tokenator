package controllers

import services.TokenService

import play.api.Logger
import play.api.mvc.{Result, Request, ActionBuilder}
import play.api.mvc.Results._

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by paullawler on 8/23/15.
 */
trait ActionBuilders {

//  def TrustedAction(tokenService: TokenService) = new ActionBuilder[Request] {
//    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
//      tokenService.authenticate(request) match {
//        case Success(futureAccount) => block(request)
//        case Failure(e) =>
//          Logger.info(s"Authenticated request failed. [URL: $request REASON: ${e.getMessage}]")
//          Future.successful(Unauthorized)
//      }
//    }
//  }

}