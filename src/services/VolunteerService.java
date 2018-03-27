package services;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import beans.User;
import dataRW.EmergencySituationsRW;
import dataRW.UsersRW;
import dto.EmergencySituationSimpleDTO;
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
	
	/*
	 * Get logged volunteer's emergency situations
	 */
	@GET
	@Path("/volunteer-emergency-situations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVolunteerEmergencySituations(){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == true){
			return Response.status(Status.FORBIDDEN).build();
		}
		List<EmergencySituationSimpleDTO> situations = Utils.getEmergencySituationsRW(context).getEmergencySituations().values()
				.stream()
				.filter(e -> e.getStatus() == 1 && e.getVolunteer() != null && e.getVolunteer().getId() == user.getId())
				.map(e -> new EmergencySituationSimpleDTO(
						e.getId(), 
						e.getName(), 
						e.getDistrict(), 
						e.getDateTime(), 
						e.getTerritory().getName(),
						e.getUrgentLevel().toString(), 
						e.getVolunteer() != null ? e.getVolunteer().getFirstName() + " " + e.getVolunteer().getLastName() : null, 
						e.getVolunteer() != null ? e.getVolunteer().getUsername() : null))
				.sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
				.collect(Collectors.toList());
		System.out.println(situations.size());
		return Response.status(Status.OK).entity(situations).build();
	}
}
