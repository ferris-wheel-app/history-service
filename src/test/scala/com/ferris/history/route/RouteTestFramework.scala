package com.ferris.history.route

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import com.ferris.microservice.exceptions.ApiExceptionFormats
import com.ferris.microservice.service.{Envelope, MicroServiceConfig}
import com.ferris.history.contract.format.HistoryRestFormats
import com.ferris.history.server.HistoryServer
import com.ferris.history.service.HistoryServiceComponent
import org.scalatest.{FunSpec, Matchers, Outcome}
import org.scalatest.mockito.MockitoSugar.mock
import spray.json._

trait MockHistoryServiceComponent extends HistoryServiceComponent {
  override val historyService: HistoryService = mock[HistoryService]
}

trait RouteTestFramework extends FunSpec with ScalatestRouteTest with HistoryRestFormats with ApiExceptionFormats with Matchers {

  var testServer: HistoryServer with HistoryServiceComponent = _
  var route: Route = _

  implicit def envFormat[T](implicit ev: JsonFormat[T]): RootJsonFormat[Envelope[T]] = jsonFormat2(Envelope[T])

  override def withFixture(test: NoArgTest): Outcome = {
    testServer = new HistoryServer with MockHistoryServiceComponent {
      override implicit lazy val system = ActorSystem()

      override implicit lazy val executor = system.dispatcher

      override implicit lazy val materializer = ActorMaterializer()

      override implicit val routeEc = scala.concurrent.ExecutionContext.global

      override val config = MicroServiceConfig

      override val logger: LoggingAdapter = Logging(system, getClass)
    }

    route = testServer.route

    super.withFixture(test)
  }
}
