package com.ferris.history.service.conversions

import com.ferris.history.contract.resource.Resources.Out.MessageView
import com.ferris.history.model.Model.Message

object ModelToView {

  implicit class MessageConversion(message: Message) {
    def toView: MessageView = {
      MessageView(
        uuid = message.uuid,
        sender = message.sender,
        content = message.content
      )
    }
  }
}
