package pl.pollub.bsi.infrastructure.jooq

import io.micronaut.context.annotation.Factory
import javax.sql.CommonDataSource
import javax.sql.DataSource

@Factory
class JooqConfigurationFactory(private val dataSource: DataSource) {


}