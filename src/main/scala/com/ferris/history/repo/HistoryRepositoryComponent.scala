package com.ferris.history.repo

import java.util.UUID

import com.ferris.history.command.Commands.{CreateMessage, UpdateMessage}
import com.ferris.history.db.TablesComponent
import com.ferris.history.db.conversions.TableConversions
import com.ferris.history.model.Model.Message
import com.ferris.history.service.exceptions.Exceptions.MessageNotFoundException

import scala.concurrent.{ExecutionContext, Future}

trait HistoryRepositoryComponent {

  val repo: HistoryRepository

  trait HistoryRepository {
    def createMessage(creation: CreateMessage): Future[Message]

    def updateMessage(uuid: UUID, update: UpdateMessage): Future[Message]

    def getMessages: Future[Seq[Message]]

    def getMessage(uuid: UUID): Future[Option[Message]]

    def deleteMessage(uuid: UUID): Future[Boolean]
  }
}

trait SqlHistoryRepositoryComponent extends HistoryRepositoryComponent {
  this: TablesComponent =>

  lazy val tableConversions = new TableConversions(tables)
  import tableConversions.tables._
  import tableConversions.tables.profile.api._
  import tableConversions._

  implicit val repoEc: ExecutionContext
  override val repo = new SqlHistoryRepository
  val db: tables.profile.api.Database

  class SqlHistoryRepository extends HistoryRepository {

    // Create endpoints
    override def createMessage(creation: CreateMessage): Future[Message] = {
      val row = MessageRow(
        id = 0L,
        uuid = UUID.randomUUID,
        sender = creation.sender,
        content = creation.content
      )
      val action = (MessageTable returning MessageTable.map(_.id) into ((message, id) => message.copy(id = id))) += row
      db.run(action) map (row => row.asMessage)
    }

    // Update endpoints
    override def updateMessage(uuid: UUID, update: UpdateMessage): Future[Message] = {
      val query = messageByUuid(uuid).map(message => (message.sender, message.content))
      val action = getMessageAction(uuid).flatMap { maybeObj =>
        maybeObj map { old =>
          query.update(update.sender.getOrElse(old.sender), update.content.getOrElse(old.content))
            .andThen(getMessageAction(uuid).map(_.head))
        } getOrElse DBIO.failed(MessageNotFoundException())
      }.transactionally
      db.run(action).map(row => row.asMessage)
    }

    // Get endpoints
    override def getMessages: Future[Seq[Message]] = {
      db.run(MessageTable.result.map(_.map(_.asMessage)))
    }

    override def getMessage(uuid: UUID): Future[Option[Message]] = {
      db.run(getMessageAction(uuid).map(_.map(_.asMessage)))
    }

    // Delete endpoints
    override def deleteMessage(uuid: UUID): Future[Boolean] = {
      val action = messageByUuid(uuid).delete
      db.run(action).map(_ > 0)
    }

    private def getMessageAction(uuid: UUID) = {
      messageByUuid(uuid).result.headOption
    }

    // Queries
    private def messageByUuid(uuid: UUID) = {
      MessageTable.filter(_.uuid === uuid.toString)
    }
  }
}
