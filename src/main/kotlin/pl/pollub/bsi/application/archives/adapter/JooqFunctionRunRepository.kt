package pl.pollub.bsi.application.archives.adapter

import io.micronaut.context.annotation.Context
import nu.studer.sample.Tables.FUNCTION_RUN
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import pl.pollub.bsi.domain.archives.FunctionRun
import pl.pollub.bsi.domain.archives.api.FunctionName
import pl.pollub.bsi.domain.archives.port.FunctionRunRepository
import kotlin.streams.toList

@Context
internal class JooqFunctionRunRepository(private val dslContext: DSLContext) : FunctionRunRepository {
    override fun save(functionRun: FunctionRun) {
        dslContext.insertInto(FUNCTION_RUN)
            .columns(
                field(FUNCTION_RUN.USER_ID),
                field(FUNCTION_RUN.FUNCTION_NAME)
            )
            .values(
                functionRun.userId,
                functionRun.functionName.name
            )
            .execute()
    }

    override fun findAllByUserId(userId: Long): List<FunctionRun> {
        return dslContext.selectFrom(FUNCTION_RUN)
            .where(FUNCTION_RUN.USER_ID.eq(userId))
            .fetch()
            .stream()
            .map {
                FunctionRun(
                    it.getValue(FUNCTION_RUN.ID),
                    it.getValue(FUNCTION_RUN.USER_ID),
                    FunctionName.valueOf(it.getValue(FUNCTION_RUN.FUNCTION_NAME))
                )
            }
            .toList()
    }
}