package com.ferris.history.server

import akka.http.scaladsl.server.ExceptionHandler
import com.ferris.microservice.exceptions.ApiExceptions
import com.ferris.history.service.exceptions.Exceptions.MessageNotFoundException

object HistoryExceptionHandler {

  val handler: ExceptionHandler = ExceptionHandler {
    case e: MessageNotFoundException => throw ApiExceptions.NotFoundException("MessageNotFound", e.message, Some(ApiExceptions.NotFoundPayload("uuid")))
  }
}
