package service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Widgets
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet
import settings.Settings

object DatabaseFactory {

    fun init(create: Boolean = true) {
        Database.connect(hikariH2())
        if (!create) return
        transaction {
            create(Widgets)
            if ("""
        SELECT t.* FROM PUBLIC.WIDGETS t
        LIMIT 1
        """.execSQLAndMap { it.getObject("NAME") }.isEmpty()) {
                Widgets.insert {
                    it[name] = "widget one"
                    it[quantity] = 27
                    it[dateUpdated] = System.currentTimeMillis()
                    it[threadM] = java.lang.Thread.activeCount()
                    it[thread1] = java.lang.Thread.activeCount()
                    it[thread2] = java.lang.Thread.activeCount()
                }
                Widgets.insert {
                    it[name] = "widget two"
                    it[quantity] = 14
                    it[dateUpdated] = System.currentTimeMillis()
                    it[threadM] = java.lang.Thread.activeCount()
                    it[thread1] = java.lang.Thread.activeCount()
                    it[thread2] = java.lang.Thread.activeCount()
                }
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = "jdbc:v://localhost:5432/ktor_exposed"
        config.username = "postgres"
        config.password = Settings.passwordPostgresql
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    private fun hikariH2(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:tcp://localhost/~/test"
        config.username = "sa"
        config.password = Settings.passwordH2
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
            block: () -> T): T =
            withContext(Dispatchers.IO) {
                transaction { block() }
            }

    private fun <T : Any> String.execSQLAndMap(transform: (ResultSet) -> T): List<T> {
        val result = arrayListOf<T>()
        TransactionManager.current().exec(this) { rs ->
            while (rs.next()) {
                result += transform(rs)
            }
        }
        return result
    }

}