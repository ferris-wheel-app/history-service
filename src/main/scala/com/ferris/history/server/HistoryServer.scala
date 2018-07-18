package com.ferris.history.server

import akka.http.scaladsl.server.Route
import com.ferris.microservice.service.MicroService
import com.ferris.history.route.HistoryRoute
import com.ferris.history.service.HistoryServiceComponent

abstract class HistoryServer extends MicroService with HistoryRoute {
  this: HistoryServiceComponent =>

  def route: Route = api(
    externalRoutes = handleExceptions(HistoryExceptionHandler.handler) { historyRoute }
  )
}
