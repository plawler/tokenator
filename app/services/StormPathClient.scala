package services

import java.util.concurrent.TimeUnit

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.application.{Applications, Application}
import com.stormpath.sdk.cache.Caches._
import com.stormpath.sdk.client.{Clients, Client}
import play.api.Logger
import play.api.Play.current

import scala.collection.JavaConversions._


trait StormpathClient {
  val apiKeyId = current.configuration.getString("stormpath.apikey.id").getOrElse("MISSING API ID")
  val apiKeySecret = current.configuration.getString("stormpath.apikey.secret").getOrElse("MISSING API SECRET")
  val appNameFilter = current.configuration.getString("stormpath.app.filter").getOrElse("")
  //  val applications = current.configuration.get("stormpath.apps.registry").getOrElse("MISSING APPLICATION REGISTRY")

  lazy val client: Client = {

    val apiKey = ApiKeys.builder().setId(apiKeyId).setSecret(apiKeySecret).build()

    Clients.builder()
      .setApiKey(apiKey)
      .setCacheManager(newCacheManager()
                        .withDefaultTimeToLive(1, TimeUnit.DAYS)
                        .withDefaultTimeToIdle(2, TimeUnit.HOURS)
                        .withCache(forResource(classOf[Account])
                                    .withTimeToLive(1, TimeUnit.HOURS)
                                    .withTimeToIdle(30, TimeUnit.MINUTES)).build() )
      .build()
  }

  val applications = scala.collection.mutable.Map[String, String]()

  def forApplication(name: String): Option[Application] = {
    val href = applications.get(name)
    href match {
      case Some(url) => Some(client.getResource(url, classOf[Application]))
      case _ => None
    }
  }

  def loadApps() = {
    applications.empty
    Logger.debug(s"App name filter: $appNameFilter")
    val apps = client.getCurrentTenant.getApplications(Applications.where(Applications.name().startsWithIgnoreCase(appNameFilter)))
    apps.foreach { application =>
      Logger.info(s"Application name=${application.getName} href=${application.getHref}")
      applications += (application.getName -> application.getHref)
    }
  }

}

object StormpathClient {

  private lazy val instance = new StormpathClient { loadApps() }

  def apply() = instance

}
