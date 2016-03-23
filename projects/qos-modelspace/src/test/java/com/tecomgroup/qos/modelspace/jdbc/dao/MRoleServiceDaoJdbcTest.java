package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.domain.rbac.PredefinedRoles;
import com.tecomgroup.qos.modelspace.jdbc.dao.MRoleServiceDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Created by kiselev.a on Tue Mar  1 11:27:47 2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MRoleServiceDaoJdbcTest {

	@Autowired
	protected MRoleServiceDao provider;

	@Autowired
	DataSource ds;

	@Before
	public void setup() {
	JdbcTemplate jt = new  JdbcTemplate(ds);
		Long maxId = jt.query
			("select max(id) from (select id from uisubject union all select id from mrole)",
			 new ResultSetExtractor<Long>() {
				 public Long extractData(ResultSet rs) throws SQLException,  DataAccessException {
					 if(rs.isBeforeFirst()) {
						 rs.next();
						 return rs.getLong(1); 
					 }
					 return null;
				 }
			 });

		if(maxId != null) {
			jt.execute("alter SEQUENCE rbac_id_seq START WITH "
					   + (maxId + 1)
					   + " INCREMENT BY 1");
		} else {
			Assert.fail("Max sequence id is null");
		}
	}

	@Test
	public void successfullCreateMRole() {
		try {
			MRole test = new MRole("testUser",
								   PermissionScope.CHANNEL_VIEW,
								   PermissionScope.MAIN,
								   PermissionScope.LIVE_VIDEO,
								   PermissionScope.REPORTS);
			Assert.assertNotNull(provider.insertRole(test));
			MRole retrievedRole = provider.getRole("testUser");
			Assert.assertTrue(test.equals(retrievedRole));

		} catch(SQLException sqle) {
			Assert.fail(sqle.getMessage());
		}
	}

	@Test
	public void successfullUpdateMRole() {
		try {
			MRole test = new MRole("testUser2",
								   PermissionScope.CHANNEL_VIEW,
								   PermissionScope.MAIN,
								   PermissionScope.LIVE_VIDEO,
								   PermissionScope.REPORTS);
			provider.insertRole(test);
			
			test = provider.getRole("testUser2");
			test.getSubjects().add(PermissionScope.ALERTS);
			provider.updateRole(test);
			MRole retrievedRole = provider.getRole("testUser2");
			Assert.assertTrue(retrievedRole.equals(test));

		} catch(SQLException sqle) {
			Assert.fail(sqle.getMessage());
		}
	}
}
