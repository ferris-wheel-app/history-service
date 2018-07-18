package com.ferris.history.route

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.model.StatusCodes.Success
import com.ferris.history.contract.resource.Resources.Out.{DeletionResult, MessageView}
import com.ferris.history.model.Model.Message
import com.ferris.history.service.conversions.ModelToView._
import com.ferris.history.service.exceptions.Exceptions.MessageNotFoundException

trait ResponseMappings {

  def mapMessage(response: Option[Message]): (Success, MessageView) = response match {
    case Some(message) => (StatusCodes.OK, message.toView)
    case None => throw MessageNotFoundException()
  }

  def mapDeletion(deleted: Boolean): (Success, DeletionResult) =
    if (deleted) (StatusCodes.OK, DeletionResult.successful)
    else (StatusCodes.OK, DeletionResult.unsuccessful)

  def mapUpdate(updated: Boolean): (StatusCode, String) =
    if (updated) (StatusCodes.OK, "updated")
    else (StatusCodes.NotModified, "not updated")
}
