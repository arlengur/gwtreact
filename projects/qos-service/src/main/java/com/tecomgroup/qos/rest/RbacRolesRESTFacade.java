package com.tecomgroup.qos.rest;

/**
 * Created by kiselev.a on Mon Mar 14 19:22:19 2016.
 */
import java.util.*;
import java.sql.SQLException;
import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.domain.rbac.MRoleDTO;
import com.tecomgroup.qos.domain.rbac.MRoleInfoDTO;
import com.tecomgroup.qos.service.rbac.RolesService;

@Path("rbac/roles")
@Component
public class RbacRolesRESTFacade {
    @Qualifier("rolesService")
	@Autowired
	private RolesService rolesService;

	@GET
    @Path("list")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<MRoleInfoDTO> listRoles() {
        return rolesService.getRolesInfo();
    }

	@POST
    @Path("create")
	@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public void createRole(MRoleDTO src) throws IOException {
		try {
			rolesService.saveRole(MRoleDTO.toEntity(src));
		} catch(SQLException e) {
			// TODO: log
			throw new IOException("Role save error");
		}
    }

	@DELETE
    @Path("delete")
	@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public void deleteRoles(Map<String, String[]> names) throws IOException {
		try {
			rolesService.deleteRoles(names.get("names"));
		} catch(SQLException e) {
			// TODO: log
			throw new IOException("Roles delete error");
		}
    }
}
