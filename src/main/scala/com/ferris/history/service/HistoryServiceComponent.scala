package com.ferris.history.service

import java.util.UUID

import com.ferris.history.command.Commands.{CreateMessage, UpdateMessage}
import com.ferris.history.model.Model.Message
import com.ferris.history.repo.HistoryRepositoryComponent

import scala.concurrent.{ExecutionContext, Future}

trait HistoryServiceComponent {
  val historyService: HistoryService

  trait HistoryService {
    def createMessage(creation: CreateMessage)(implicit ex: ExecutionContext): Future[Message]

    def updateMessage(uuid: UUID, update: UpdateMessage)(implicit ex: ExecutionContext): Future[Message]

    def getMessages(implicit ex: ExecutionContext): Future[Seq[Message]]

    def getMessage(uuid: UUID)(implicit ex: ExecutionContext): Future[Option[Message]]

    def deleteMessage(uuid: UUID)(implicit ex: ExecutionContext): Future[Boolean]
  }
}

trait DefaultHistoryServiceComponent extends HistoryServiceComponent {
  this: HistoryRepositoryComponent =>

  override val historyService = new DefaultHistoryService

  class DefaultHistoryService extends HistoryService {

    override def createMessage(creation: CreateMessage)(implicit ex: ExecutionContext): Future[Message] = {
      repo.createMessage(creation)
    }

    override def updateMessage(uuid: UUID, update: UpdateMessage)(implicit ex: ExecutionContext): Future[Message] = {
      repo.updateMessage(uuid, update)
    }

    override def getMessages(implicit ex: ExecutionContext): Future[Seq[Message]] = {
      repo.getMessages
    }

    override def getMessage(uuid: UUID)(implicit ex: ExecutionContext): Future[Option[Message]] = {
      repo.getMessage(uuid)
    }

    override def deleteMessage(uuid: UUID)(implicit ex: ExecutionContext): Future[Boolean] = {
      repo.deleteMessage(uuid)
    }
  }
}
