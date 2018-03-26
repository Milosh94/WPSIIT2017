package services;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import beans.Territory;
import beans.User;
import dataRW.TerritoriesRW;
import dataRW.UsersRW;
import dto.UserDTO;
import util.Utils;

@Path("")
public class VolunteerService {

	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext context;
	
	/*
	 * Get all volunteers
	 */
	@GET
	@Path("/volunteers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVolunteers(){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		UsersRW usersRW = Utils.getUsersRW(context);
		List<UserDTO> volunteers = usersRW.getUsers().values()
				.stream()
				.filter(u -> u.isAdmin() == false)
				.map(u -> new UserDTO(
						u.getUsername(), 
						u.getFirstName(), 
						u.getLastName(), 
						u.getPhone(), 
						u.getEmail(), 
						u.getTerritory(), 
						u.getPicture(), 
						u.isBlocked(), 
						u.isAdmin()
					))
				.collect(Collectors.toList());
		return Response.status(Status.OK).entity(volunteers).build();
	}
}
