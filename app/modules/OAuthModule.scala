package modules

import com.google.inject.{Scopes, TypeLiteral, AbstractModule}
import models.Account
import services.MyDataHandler

import scalaoauth2.provider.DataHandler

/**
 * Created by paullawler on 8/22/15.
 */
class OAuthModule extends AbstractModule {
  override def configure(): Unit = {
    bind(new TypeLiteral[DataHandler[Account]] {}).to(classOf[MyDataHandler]).in(Scopes.SINGLETON)
  }
}
