package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.*;
import org.hsqldb.types.Types;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JdbcAlertServiceDao implements AlertServiceDao {
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall insertAlertTypeCall;
    private SimpleJdbcCall closeAlertReport;
    private SimpleJdbcCall openAlertReport;
    private SimpleJdbcCall insertUpdates;
    private SimpleJdbcCall insertAlert;
    private SimpleJdbcCall updateAlert;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.insertAlertTypeCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("insert_alert_type")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("in_description","in_displayname","in_displaytemplate","in_name","in_probablecause")
                .declareParameters(
                        new SqlOutParameter("out_id", Types.BIGINT),
                        new SqlParameter("in_description", Types.VARCHAR),
                        new SqlParameter("in_displayname", Types.VARCHAR),
                        new SqlParameter("in_displaytemplate", Types.VARCHAR),
                        new SqlParameter("in_name", Types.VARCHAR),
                        new SqlParameter("in_probablecause", Types.VARCHAR)
                );

        this.closeAlertReport = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("close_alert_report")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("in_id", "in_end_date_time")
                .declareParameters(
                        new SqlOutParameter("out_count", Types.BIGINT),
                        new SqlOutParameter("out_id", Types.BIGINT),
                        new SqlParameter("in_id", Types.BIGINT),
                        new SqlParameter("in_end_date_time", Types.TIMESTAMP)
                );

        this.openAlertReport = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("open_alert_report")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("in_id", "in_end_date_time", "in_severity", "in_start_date_time")
                .declareParameters(
                        new SqlOutParameter("out_id", Types.BIGINT),
                        new SqlParameter("in_id", Types.BIGINT),
                        new SqlParameter("in_end_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_severity", Types.VARCHAR),
                        new SqlParameter("in_start_date_time", Types.TIMESTAMP)

                );

        this.insertUpdates = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("insert_alert_update")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("in_comment",
                        "in_date_time", "in_field", "in_new_value",
                        "in_old_value","in_update_type","in_user_name","in_alert_id")
                .declareParameters(
                        new SqlParameter("in_comment", Types.VARCHAR),
                        new SqlParameter("in_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_field", Types.VARCHAR),
                        new SqlParameter("in_new_value", Types.VARCHAR),
                        new SqlParameter("in_old_value", Types.VARCHAR),
                        new SqlParameter("in_update_type", Types.VARCHAR),
                        new SqlParameter("in_user_name", Types.VARCHAR),
                        new SqlParameter("in_alert_id", Types.BIGINT)
                );

        this.insertAlert = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("insert_alert")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("in_context", "in_extra_data", "in_perceived_severity",
                        "in_settings","in_specific_reason", "in_alert_type_id",
                        "in_acknowledged","in_acknowledgment_date_time","in_alert_count",
                        "in_cleared_date_time", "in_counts_since_last_ack", "in_creation_date_time",
                        "in_disabled","in_last_update_date_time","in_last_update_type", "in_status",
                        "in_originator_id","in_source_id","in_detection_value", "in_severity_change_date_time")
                .declareParameters(
                        new SqlOutParameter("out_alert_id", Types.BIGINT),
                        new SqlParameter("in_context", Types.VARCHAR),
                        new SqlParameter("in_extra_data", Types.VARCHAR),
                        new SqlParameter("in_perceived_severity", Types.INTEGER),
                        new SqlParameter("in_settings", Types.VARCHAR),
                        new SqlParameter("in_specific_reason", Types.VARCHAR),
                        new SqlParameter("in_alert_type_id", Types.BIGINT),
                        new SqlParameter("in_acknowledged", Types.BOOLEAN),
                        new SqlParameter("in_acknowledgment_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_alert_count", Types.BIGINT),
                        new SqlParameter("in_cleared_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_counts_since_last_ack", Types.BIGINT),
                        new SqlParameter("in_creation_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_disabled", Types.BOOLEAN),
                        new SqlParameter("in_last_update_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_last_update_type", Types.VARCHAR),
                        new SqlParameter("in_status", Types.VARCHAR),
                        new SqlParameter("in_originator_id", Types.BIGINT),
                        new SqlParameter("in_source_id", Types.BIGINT),
                        new SqlParameter("in_detection_value", Types.DOUBLE),
                        new SqlParameter("in_severity_change_date_time", Types.TIMESTAMP));

        this.updateAlert = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("update_alert")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("in_alert_id","in_context", "in_extra_data", "in_perceived_severity",
                        "in_specific_reason",
                        "in_acknowledged","in_acknowledgment_date_time","in_alert_count",
                        "in_cleared_date_time", "in_counts_since_last_ack", "in_creation_date_time",
                        "in_disabled","in_last_update_date_time","in_last_update_type", "in_status",
                        "in_detection_value", "in_severity_change_date_time")
                .declareParameters(
                        new SqlParameter("in_alert_id", Types.BIGINT),
                        new SqlParameter("in_context", Types.VARCHAR),
                        new SqlParameter("in_extra_data", Types.VARCHAR),
                        new SqlParameter("in_perceived_severity", Types.INTEGER),

                        new SqlParameter("in_specific_reason", Types.VARCHAR),

                        new SqlParameter("in_acknowledged", Types.BOOLEAN),
                        new SqlParameter("in_acknowledgment_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_alert_count", Types.BIGINT),
                        new SqlParameter("in_cleared_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_counts_since_last_ack", Types.BIGINT),
                        new SqlParameter("in_creation_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_disabled", Types.BOOLEAN),
                        new SqlParameter("in_last_update_date_time", Types.TIMESTAMP),
                        new SqlParameter("in_last_update_type", Types.VARCHAR),
                        new SqlParameter("in_status", Types.VARCHAR),

                        new SqlParameter("in_detection_value", Types.DOUBLE),
                        new SqlParameter("in_severity_change_date_time", Types.TIMESTAMP));

    }

    @Override
    public AlertDTO getAlert(MAlertIndication indication) {

        final String sourceKey = indication.getSource().getKey();
        final String alertType = indication.getAlertType().getName();
        String settings = indication.getSettings();
        final String originatorKey = indication.getOriginator().getKey();
        return getAlert(sourceKey, alertType, originatorKey, settings);
    }

    private AlertDTO getAlert(final String sourceKey, final String alertType,final String originatorKey, final String settings) {
        try {
        return this.jdbcTemplate.queryForObject
                ("SELECT *  FROM malert" +
                                " left join magenttask on malert.source_id = magenttask.id " +
                                " left join malerttype on malert.alerttype_id = malerttype.id " +
                                " left join mpolicy on malert.originator_id = mpolicy.id " +
                                " WHERE magenttask.entity_key = ? AND " +
                                " malerttype.name = ? AND " +
                                " malert.settings = ? AND " +
                                " mpolicy.entity_key = ?",
                        new Object[]{sourceKey, alertType, settings, originatorKey},
                        new RowMapper<AlertDTO>() {
                            public AlertDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                                AlertDTO alert = new AlertDTO();
                                alert.setId(rs.getBigDecimal("id"));
                                alert.setContext(rs.getString("context"));
                                alert.setExtradata(rs.getString("extradata"));
                                alert.setPerceivedseverity(PerceivedSeverity.getByOrdinal(rs.getInt("perceivedseverity")));
                                alert.setSettings(rs.getString("settings"));
                                alert.setSpecificreason(SpecificReason.getByString(rs.getString("specificreason")));
                                alert.setAlerttype_id(rs.getBigDecimal("alerttype_id"));
                                alert.setAlertTypeName(alertType);
                                alert.setAcknowledged(rs.getBoolean("acknowledged"));
                                alert.setAcknowledgmentdatetime(rs.getTimestamp("acknowledgmentdatetime"));
                                alert.setAlert_count(rs.getBigDecimal("alert_count"));
                                alert.setCleareddatetime(rs.getTimestamp("cleareddatetime"));
                                alert.setCountsincelastack(rs.getBigDecimal("countsincelastack"));
                                alert.setCreationdatetime(rs.getTimestamp("creationdatetime"));
                                alert.setDisabled(rs.getBoolean("disabled"));
                                alert.setLastupdatedatetime(rs.getTimestamp("lastupdatedatetime"));
                                alert.setLastupdatetype(MAlertType.UpdateType.getByString(rs.getString("lastupdatetype")));
                                alert.setStatus(MAlertType.Status.getByString(rs.getString("status")));
                                alert.setOriginator_id(rs.getBigDecimal("Originator_id"));
                                alert.setOriginatorName(originatorKey);
                                alert.setSource_id(rs.getBigDecimal("Source_id"));
                                alert.setSourceName(sourceKey);
                                alert.setDetectionvalue(rs.getDouble("Detectionvalue"));
                                alert.setSeveritychangedatetime(rs.getTimestamp("Severitychangedatetime"));
                                return alert;
                            }
                        });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AlertDTO> getActiveAlerts() {
        try {
            return this.jdbcTemplate.query
                    ("SELECT malert.*,magenttask.*,malerttype.*,mpolicy.*,magenttask.entity_key as source_key,mpolicy.entity_key as originator_key,malerttype.name as malert_type_name FROM malert" +
                                    " left join magenttask on malert.source_id = magenttask.id " +
                                    " left join malerttype on malert.alerttype_id = malerttype.id " +
                                    " left join mpolicy on malert.originator_id = mpolicy.id " +
                                    " WHERE " +
                                    " malert.disabled = false AND " +
                                    " malert.status = ?",
                            new Object[]{MAlertType.Status.ACTIVE.toString()},
                            new RowMapper<AlertDTO>() {
                                public AlertDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    AlertDTO alert = new AlertDTO();
                                    alert.setId(rs.getBigDecimal("id"));
                                    alert.setContext(rs.getString("context"));
                                    alert.setExtradata(rs.getString("extradata"));
                                    alert.setPerceivedseverity(PerceivedSeverity.getByOrdinal(rs.getInt("perceivedseverity")));
                                    alert.setSettings(rs.getString("settings"));
                                    alert.setSpecificreason(SpecificReason.getByString(rs.getString("specificreason")));
                                    alert.setAlerttype_id(rs.getBigDecimal("alerttype_id"));
                                    alert.setAlertTypeName(rs.getString("malert_type_name"));
                                    alert.setAcknowledged(rs.getBoolean("acknowledged"));
                                    alert.setAcknowledgmentdatetime(rs.getTimestamp("acknowledgmentdatetime"));
                                    alert.setAlert_count(rs.getBigDecimal("alert_count"));
                                    alert.setCleareddatetime(rs.getTimestamp("cleareddatetime"));
                                    alert.setCountsincelastack(rs.getBigDecimal("countsincelastack"));
                                    alert.setCreationdatetime(rs.getTimestamp("creationdatetime"));
                                    alert.setDisabled(rs.getBoolean("disabled"));
                                    alert.setLastupdatedatetime(rs.getTimestamp("lastupdatedatetime"));
                                    alert.setLastupdatetype(MAlertType.UpdateType.getByString(rs.getString("lastupdatetype")));
                                    alert.setStatus(MAlertType.Status.getByString(rs.getString("status")));
                                    alert.setOriginator_id(rs.getBigDecimal("Originator_id"));
                                    alert.setOriginatorName(rs.getString("originator_key"));
                                    alert.setSource_id(rs.getBigDecimal("Source_id"));
                                    alert.setSourceName(rs.getString("source_key"));
                                    alert.setDetectionvalue(rs.getDouble("Detectionvalue"));
                                    alert.setSeveritychangedatetime(rs.getTimestamp("Severitychangedatetime"));
                                    return alert;
                                }
                            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void insertAlert(AlertDTO alert) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("in_context", alert.getContext())
                .addValue("in_extra_data", alert.getExtradata())
                .addValue("in_perceived_severity", alert.getPerceivedseverity().ordinal())
                .addValue("in_settings", alert.getSettings())
                .addValue("in_specific_reason", alert.getSpecificreason().name())
                .addValue("in_alert_type_id", alert.getAlerttype_id().longValue())
                .addValue("in_acknowledged", alert.isAcknowledged())
                .addValue("in_acknowledgment_date_time", alert.getAcknowledgmentdatetime())
                .addValue("in_alert_count", alert.getAlert_count())
                .addValue("in_cleared_date_time", alert.getCleareddatetime())
                .addValue("in_counts_since_last_ack", alert.getCountsincelastack())
                .addValue("in_creation_date_time", alert.getCreationdatetime())
                .addValue("in_disabled", alert.isDisabled())
                .addValue("in_last_update_date_time", alert.getLastupdatedatetime())
                .addValue("in_last_update_type", alert.getLastupdatetype().name())
                .addValue("in_status", alert.getStatus().name())
                .addValue("in_detection_value", (alert.getDetectionvalue() != null) ? alert.getDetectionvalue().doubleValue(): null)
                .addValue("in_originator_id", alert.getOriginator_id().longValue())
                .addValue("in_source_id",alert.getSource_id().longValue())
                .addValue("in_severity_change_date_time", alert.getSeveritychangedatetime());

        Map out = this.insertAlert.execute(in);
        alert.setId(new BigDecimal((Long) out.get("out_alert_id")));
    }

    @Override
    public void updateAlert(AlertDTO alert) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("in_alert_id", alert.getId().longValue())
                .addValue("in_context", alert.getContext())
                .addValue("in_extra_data", alert.getExtradata())
                .addValue("in_perceived_severity", alert.getPerceivedseverity().ordinal())
                .addValue("in_specific_reason", alert.getSpecificreason().name())
                .addValue("in_acknowledged", alert.isAcknowledged())
                .addValue("in_acknowledgment_date_time", alert.getAcknowledgmentdatetime())
                .addValue("in_alert_count", alert.getAlert_count())
                .addValue("in_cleared_date_time", alert.getCleareddatetime())
                .addValue("in_counts_since_last_ack", alert.getCountsincelastack())
                .addValue("in_creation_date_time", alert.getCreationdatetime())
                .addValue("in_disabled", alert.isDisabled())
                .addValue("in_last_update_date_time", alert.getLastupdatedatetime())
                .addValue("in_last_update_type", alert.getLastupdatetype().name())
                .addValue("in_status", alert.getStatus().name())
                .addValue("in_detection_value", (alert.getDetectionvalue() != null) ? alert.getDetectionvalue().doubleValue(): null)
                .addValue("in_severity_change_date_time",alert.getSeveritychangedatetime());

        this.updateAlert.execute(in);
    }

    @Override
    public BigDecimal getAlertTypeId(String alertTypeName) {
        try {
            return this.jdbcTemplate.queryForObject
                    ("SELECT id FROM MAlertType WHERE name = ?",
                            new Object[]{alertTypeName},
                            BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public BigDecimal getSourceTaskId(String sourceKey) {
        try {
        return this.jdbcTemplate.queryForObject
                ("SELECT id FROM MAgentTask where entity_key = ?",
                        new Object[]{sourceKey},
                        BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getAgentKey(String sourceKey) {
        try {
            return this.jdbcTemplate.queryForObject
                    ("SELECT magent.entity_key FROM magent" +
                      "  left join mmediaagentmodule on magent.id = mmediaagentmodule.parent_id" +
                      "  left join magenttask on mmediaagentmodule.id = magenttask.parent_id" +
                      "  where magenttask.entity_key = ?",
                            new Object[]{sourceKey},
                            String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public BigDecimal getPolicyId(String originatorKey) {
        try {
        return this.jdbcTemplate.queryForObject
                ("SELECT id FROM MPolicy where entity_key = ?",
                        new Object[]{originatorKey},
                        BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    public void insertUpdates(final List<AlertUpdateDTO> updates) {
        List<Object[]> batch = new ArrayList<>();
        for (AlertUpdateDTO update : updates) {

            SqlParameterSource in = new MapSqlParameterSource()
                    .addValue("in_comment",update.getComment())
                    .addValue("in_date_time", update.getDatetime())
                    .addValue("in_field", update.getField())
                    .addValue("in_new_value",update.getNewvalue())
                    .addValue("in_old_value",update.getOldvalue())
                    .addValue("in_update_type", update.getUpdatetype().name())
                    .addValue("in_user_name", update.getUser_name())
                    .addValue("in_alert_id", update.getAlert_id().longValue());
            this.insertUpdates.execute(in);
        }
    }

    public Long openAlertReport(BigDecimal alertId,
                                   Date endDateTime,
                                   MAlertType.PerceivedSeverity severity,
                                   Date startDateTime) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("in_id", alertId)
                .addValue("in_end_date_time", endDateTime)
                .addValue("in_severity", severity.name())
                .addValue("in_start_date_time", startDateTime);
        Map out = this.openAlertReport.execute(in);
        return (Long) out.get("out_id");

    }

    public Long closeAlertReport(BigDecimal alertId,
                                  Date endDateTime) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("in_id", alertId)
                .addValue("in_end_date_time", endDateTime);

        Map out = this.closeAlertReport.execute(in);
        return (Long) out.get("out_id");
    }

    @Override
    public void registerAlertTypes(final List<MAlertType> types) {
        for (MAlertType type: types) {
            SqlParameterSource in = new MapSqlParameterSource()
                    .addValue("in_description", type.getDescription())
                    .addValue("in_displayname", type.getDisplayName())
                    .addValue("in_displaytemplate", type.getDisplayTemplate())
                    .addValue("in_name", type.getName())
                    .addValue("in_probablecause", type.getProbableCause());
            this.insertAlertTypeCall.execute(in);
        }
    }


}
