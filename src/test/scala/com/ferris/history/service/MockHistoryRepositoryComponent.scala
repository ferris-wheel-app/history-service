package com.ferris.history.service

import com.ferris.history.repo.HistoryRepositoryComponent
import org.scalatest.mockito.MockitoSugar.mock

trait MockHistoryRepositoryComponent extends HistoryRepositoryComponent {

  override val repo: HistoryRepository = mock[HistoryRepository]
}
