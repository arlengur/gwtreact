package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.rbac.*;
import org.hsqldb.types.Types;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Created by kiselev.a on Mon Feb 29 10:57:20 2016.
 */

public class JdbcMRoleServiceDao extends JdbcRbacSequenceDao implements MRoleServiceDao {
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	private class ProtoRoleContainer {
		public MRole role;
		public Long[] ids;
		
		public ProtoRoleContainer(MRole role, Long[] ids) {
			this.role = role;
			this.ids = ids;
		}
	}

	public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		super.initStoredProcedure(this.jdbcTemplate);
	}

	@Override
	@Transactional
	public List<MRole> getRolesList() {
		try {
			final JdbcRbacUISubjectServiceDao rbacUIS = new JdbcRbacUISubjectServiceDao();
			rbacUIS.setDataSource(jdbcTemplate.getDataSource());
			
			List<ProtoRoleContainer> tmpList =  this.jdbcTemplate.query 
				("SELECT *  FROM mrole",
				 new RowMapper<ProtoRoleContainer>() {
					public ProtoRoleContainer mapRow(ResultSet rs, int rn) throws SQLException,  DataAccessException {
						Object[] tmpSubjO = (Object[])rs.getArray("subjects").getArray();
						Long[] tmpSubj = new Long[tmpSubjO.length];
						
						for(int i = 0; i < tmpSubjO.length; i++) {
							tmpSubj[i] = (Long)tmpSubjO[i];
						}

						MRole role = new MRole(rs.getString("name"));						
						role.setId(rs.getLong("id")); 
						role.setComment(rs.getString("comment"));
						
						return new ProtoRoleContainer(role, tmpSubj);
					}
				});

			List<MRole> result = new ArrayList<MRole>();
			for(ProtoRoleContainer prc : tmpList) {
				Set<UISubject> subjs = null;
				if(prc.ids != null && prc.ids.length > 0) {
					subjs = new HashSet<UISubject>(rbacUIS.getSubjectById(prc.ids));
				}

				if(prc.role != null) {
					prc.role.setSubjects(subjs);
				}

				result.add(prc.role);
			}
			
			return result;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();// FIXME: logging need
			return new ArrayList<MRole>();
		}
	}

	@Override
	public MRole getRole(String name) {
		 try {
			 ProtoRoleContainer prc = this.jdbcTemplate.query
				 ("SELECT *  FROM mrole where name = ?",
				  new Object[]{name},
				  new ResultSetExtractor<ProtoRoleContainer>() { //better way - return tuple of MRole with ids
					  public ProtoRoleContainer extractData(ResultSet rs) throws SQLException,  DataAccessException {
						  if(rs.isBeforeFirst()) {
							  rs.next();

							  Object[] tmpSubjO = (Object[])rs.getArray("subjects").getArray();
							  Long[] tmpSubj = new Long[tmpSubjO.length];

							  for(int i = 0; i < tmpSubjO.length; i++) {
							  	  tmpSubj[i] = (Long)tmpSubjO[i];
							  }
							
							  MRole role = new MRole(rs.getString("name"));
							  role.setId(rs.getLong("id")); 
							  role.setComment(rs.getString("comment"));

							  return new ProtoRoleContainer(role, tmpSubj);
						  }
						  return null;
					  }
				  });

			 Set<UISubject> subjs = null;
			 if(prc != null) {
				 if(prc.ids != null) {
					 JdbcRbacUISubjectServiceDao rbacUIS = new JdbcRbacUISubjectServiceDao();
					 rbacUIS.setDataSource(jdbcTemplate.getDataSource());
					 subjs = new HashSet<UISubject>(rbacUIS.getSubjectById(prc.ids));
				 }
				 
				 if(prc.role != null) {
					 prc.role.setSubjects(subjs);
				 }
				 return prc.role;
			 }
			 return null;

		 } catch (EmptyResultDataAccessException e) {
			 e.printStackTrace();// FIXME: logging need
			 return null;
		 }
	}

	@Override
	public Long insertRole(final MRole role) throws SQLException {
		if(role.getId() == null || role.getId() < 1) {
			role.setId(getNextSeqId());
		}

		this.jdbcTemplate.update(
			"INSERT INTO mrole (id, name, subjects, comment) VALUES(?, ?, ?, ?)",
			new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) {
					try {
						Array thridParameter = ps.getConnection()
							.createArrayOf("bigint",
										   role.getSubjectsId());
						ps.setLong(1, role.getId());
						ps.setString(2, role.getName());
						ps.setArray(3, thridParameter);
						ps.setString(4, role.getComment());
						
					} catch(SQLException sqle) {
						sqle.printStackTrace(); // FIXME: logging need
					}
				}
			});
		return role.getId();
	}

	@Override	
	public void updateRole(final MRole role) throws SQLException {
		int updated = this.jdbcTemplate.update(
			"UPDATE mrole SET name=?, subjects=?, comment=? WHERE name=?",
			new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) {
					try {
						Array thridParameter = ps.getConnection()
							.createArrayOf("bigint",
										   role.getSubjectsId());
						ps.setString(1, role.getName());
						ps.setArray(2, thridParameter);
						ps.setString(3, role.getComment());
						ps.setString(4, role.getName());
						
					} catch(SQLException sqle) {
						sqle.printStackTrace(); // FIXME: logging need
					}
				}
			});
		
		if(updated == 0) {
			insertRole(role);
		}
	}

	@Override
	public void deleteRoles(String ... names) throws SQLException {
		List<Long> ids = namedJdbcTemplate.query(
			"select id from mrole where name in (:list)",
			Collections.singletonMap("list", Arrays.asList(names)),
			new RowMapper<Long>() {
				public Long mapRow(ResultSet rs, int rn) throws SQLException,  DataAccessException {
					return rs.getLong("id");
				}
			});
		
		Map<String, List<Long>> mapLong = Collections.singletonMap("list", ids);
		namedJdbcTemplate.update("DELETE FROM mrole WHERE id IN (:list)",
								 mapLong);
		
		namedJdbcTemplate.update(
			"UPDATE mrole SET "
			+ "subjects = ARRAY(SELECT UNNEST(subjects) EXCEPT SELECT UNNEST(ARRAY[ :list ]::bigint[])) "
			+ "WHERE ARRAY[ :list ]::bigint[] && subjects",
			mapLong);
	}
}
