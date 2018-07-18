package com.ferris.history.client

import java.util.UUID

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Path._
import akka.stream.ActorMaterializer
import com.ferris.service.client.{HttpServer, ServiceClient}
import com.ferris.history.contract.format.HistoryRestFormats
import com.ferris.history.contract.resource.Resources.In.{MessageCreation, MessageUpdate}
import com.ferris.history.contract.resource.Resources.Out.{DeletionResult, MessageView}

import scala.concurrent.Future

class HistoryServiceClient(val server: HttpServer, implicit val mat: ActorMaterializer) extends ServiceClient with HistoryRestFormats {

  def this(server: HttpServer) = this(server, server.mat)

  private val apiPath = /("api")

  private val messagesPath = "messages"

  def createMessage(creation: MessageCreation): Future[MessageView] =
    makePostRequest[MessageCreation, MessageView](Uri(path = apiPath / messagesPath), creation)

  def updateMessage(id: UUID, update: MessageUpdate): Future[MessageView] =
    makePutRequest[MessageUpdate, MessageView](Uri(path = apiPath / messagesPath / id.toString), update)

  def message(id: UUID): Future[Option[MessageView]] =
    makeGetRequest[Option[MessageView]](Uri(path = apiPath / messagesPath / id.toString))

  def messages: Future[List[MessageView]] =
    makeGetRequest[List[MessageView]](Uri(path = apiPath / messagesPath))

  def deleteMessage(id: UUID): Future[DeletionResult] =
    makeDeleteRequest[DeletionResult](Uri(path = apiPath / messagesPath / id.toString))
}
