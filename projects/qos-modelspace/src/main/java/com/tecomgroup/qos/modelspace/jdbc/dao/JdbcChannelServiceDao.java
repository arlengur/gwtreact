package com.tecomgroup.qos.modelspace.jdbc.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by galin.a on 26.10.2015.
 */
@Component
public class JdbcChannelServiceDao implements ChannelServiceDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
    }

    @Override
    public Boolean hasName(Long userId, String name) {
        try {
            return this.jdbcTemplate.query
                    ("SELECT name FROM msetconfiguration m WHERE m.userid = ? AND upper(m.name) like ?",
                            new Object[]{userId, name.toUpperCase()},
                            new RowMapper<String>() {
                                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    return rs.getString(1);
                                }
                            }).size() > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
