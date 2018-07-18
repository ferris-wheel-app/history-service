package com.ferris.history.db

import com.ferris.history.table.Tables

import slick.jdbc.MySQLProfile

object MySQLTables extends Tables {
  override val profile = MySQLProfile
}

trait MySQLTablesComponent extends TablesComponent {
  val tables = MySQLTables
}
