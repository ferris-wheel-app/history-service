package com.ferris.history.repo

import com.ferris.history.db.H2TablesComponent
import com.ferris.utils.TimerComponent

import scala.concurrent.ExecutionContext
import scala.util.Random

trait H2HistoryRepositoryComponent extends SqlHistoryRepositoryComponent with H2TablesComponent with TimerComponent {

  override implicit val repoEc: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val randomSchemaName: String = "s" + Random.nextInt().toString.drop(1)
  val jdbcUrl: String = s"jdbc:h2:mem:$randomSchemaName;MODE=MySQL;DATABASE_TO_UPPER=false;INIT=CREATE SCHEMA IF NOT EXISTS $randomSchemaName;DB_CLOSE_DELAY=-1"
  import tables.profile.api._
  override val db: Database = Database.forURL(jdbcUrl, driver = "org.h2.Driver")

  override val repo = new SqlHistoryRepository
}
