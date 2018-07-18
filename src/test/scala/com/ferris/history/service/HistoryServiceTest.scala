package com.ferris.history.service

import java.util.UUID

import com.ferris.history.sample.SampleData.{domain => SD}
import com.ferris.history.service.exceptions.Exceptions._
import org.mockito.Matchers.{eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class HistoryServiceTest extends FunSpec with ScalaFutures with Matchers {

  implicit val defaultTimeout: PatienceConfig = PatienceConfig(scaled(15.seconds))

  def newServer = new DefaultHistoryServiceComponent with MockHistoryRepositoryComponent {
    override val historyService: DefaultHistoryService = new DefaultHistoryService()
  }

  describe("a history service") {
    describe("handling messages") {
      it("should be able to create a message") {
        val server = newServer
        when(server.repo.createMessage(SD.messageCreation)).thenReturn(Future.successful(SD.message))
        whenReady(server.historyService.createMessage(SD.messageCreation)) { result =>
          result shouldBe SD.message
          verify(server.repo, times(1)).createMessage(SD.messageCreation)
          verifyNoMoreInteractions(server.repo)
        }
      }

      it("should be able to update a message") {
        val server = newServer
        val id = UUID.randomUUID
        val updated = SD.message
        when(server.repo.updateMessage(eqTo(id), eqTo(SD.messageUpdate))).thenReturn(Future.successful(updated))
        whenReady(server.historyService.updateMessage(id, SD.messageUpdate)) { result =>
          result shouldBe updated
          verify(server.repo, times(1)).updateMessage(eqTo(id), eqTo(SD.messageUpdate))
          verifyNoMoreInteractions(server.repo)
        }
      }

      it("should return an error thrown by the repository when a message is being updated") {
        val server = newServer
        val id = UUID.randomUUID
        val expectedException = MessageNotFoundException()
        when(server.repo.updateMessage(eqTo(id), eqTo(SD.messageUpdate))).thenReturn(Future.failed(expectedException))
        whenReady(server.historyService.updateMessage(id, SD.messageUpdate).failed) { exception =>
          exception shouldBe expectedException
          verify(server.repo, times(1)).updateMessage(eqTo(id), eqTo(SD.messageUpdate))
          verifyNoMoreInteractions(server.repo)
        }
      }

      it("should be able to retrieve a message") {
        val server = newServer
        val id = UUID.randomUUID
        when(server.repo.getMessage(id)).thenReturn(Future.successful(Some(SD.message)))
        whenReady(server.historyService.getMessage(id)) { result =>
          result shouldBe Some(SD.message)
          verify(server.repo, times(1)).getMessage(id)
          verifyNoMoreInteractions(server.repo)
        }
      }

      it("should be able to retrieve all messages") {
        val server = newServer
        val messages = Seq(SD.message, SD.message.copy(uuid = UUID.randomUUID))
        when(server.repo.getMessages).thenReturn(Future.successful(messages))
        whenReady(server.historyService.getMessages) { result =>
          result shouldBe messages
          verify(server.repo, times(1)).getMessages
          verifyNoMoreInteractions(server.repo)
        }
      }

      it("should be able to delete a message") {
        val server = newServer
        val id = UUID.randomUUID
        when(server.repo.deleteMessage(id)).thenReturn(Future.successful(true))
        whenReady(server.historyService.deleteMessage(id)) { result =>
          result shouldBe true
          verify(server.repo, times(1)).deleteMessage(id)
          verifyNoMoreInteractions(server.repo)
        }
      }
    }
  }
}
