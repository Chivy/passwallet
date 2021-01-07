package pl.pollub.bsi.application.archives.adapter

import io.micronaut.context.annotation.Context
import io.vavr.control.Option
import nu.studer.sample.Tables.DATA_CHANGE
import org.jooq.DSLContext
import pl.pollub.bsi.application.archives.dto.DataChangedEventType
import pl.pollub.bsi.domain.archives.DataChange
import pl.pollub.bsi.domain.archives.api.TableName
import pl.pollub.bsi.domain.archives.port.DataChangeRepository
import java.util.stream.Collectors

@Context
class JooqDataChangeRepository(private val dslContext: DSLContext) : DataChangeRepository {
    override fun save(dataChange: DataChange): DataChange {
        return dslContext.insertInto(DATA_CHANGE)
            .columns(
                DATA_CHANGE.USER_ID,
                DATA_CHANGE.MODIFIED_RECORD_ID,
                DATA_CHANGE.PREVIOUS_VALUE_OF_RECORD,
                DATA_CHANGE.ACTION_TYPE,
                DATA_CHANGE.TABLE_NAME
            )
            .values(
                dataChange.userId,
                dataChange.modifiedRecordId,
                dataChange.previousValueOfRecord,
                dataChange.actionType.name,
                dataChange.tableName.name
            )
            .returningResult(
                DATA_CHANGE.USER_ID,
                DATA_CHANGE.MODIFIED_RECORD_ID,
                DATA_CHANGE.PREVIOUS_VALUE_OF_RECORD,
                DATA_CHANGE.ACTION_TYPE,
                DATA_CHANGE.TABLE_NAME
            )
            .fetchOne()
            .map {
                DataChange(
                    it.getValue(DATA_CHANGE.ID),
                    it.getValue(DATA_CHANGE.USER_ID),
                    it.getValue(DATA_CHANGE.MODIFIED_RECORD_ID),
                    it.getValue(DATA_CHANGE.PREVIOUS_VALUE_OF_RECORD),
                    DataChangedEventType.valueOf(it.getValue(DATA_CHANGE.ACTION_TYPE)),
                    TableName.valueOf(it.getValue(DATA_CHANGE.TABLE_NAME))
                )
            }
    }

    override fun findAllBy(userId: Long): List<DataChange> {
        return dslContext.selectFrom(DATA_CHANGE)
            .where(DATA_CHANGE.USER_ID.eq(userId))
            .fetch()
            .stream()
            .map {
                DataChange(
                    it.getValue(DATA_CHANGE.ID),
                    it.getValue(DATA_CHANGE.USER_ID),
                    it.getValue(DATA_CHANGE.MODIFIED_RECORD_ID),
                    it.getValue(DATA_CHANGE.PREVIOUS_VALUE_OF_RECORD),
                    DataChangedEventType.valueOf(it.getValue(DATA_CHANGE.ACTION_TYPE)),
                    TableName.valueOf(it.getValue(DATA_CHANGE.TABLE_NAME))
                )
            }
            .collect(Collectors.toList())
    }

    override fun findById(archiveId: Long): Option<DataChange> {
        return Option.ofOptional(
            dslContext.selectFrom(DATA_CHANGE)
                .where(DATA_CHANGE.ID.eq(archiveId))
                .fetchOptional()
                .map {
                    DataChange(
                        it.getValue(DATA_CHANGE.ID),
                        it.getValue(DATA_CHANGE.USER_ID),
                        it.getValue(DATA_CHANGE.MODIFIED_RECORD_ID),
                        it.getValue(DATA_CHANGE.PREVIOUS_VALUE_OF_RECORD),
                        DataChangedEventType.valueOf(it.getValue(DATA_CHANGE.ACTION_TYPE)),
                        TableName.valueOf(it.getValue(DATA_CHANGE.TABLE_NAME))
                    )
                }
        )
    }
}