package services;

import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import beans.Territory;
import beans.User;
import dataRW.TerritoriesRW;
import dataRW.UsersRW;
import dto.LoginDTO;
import dto.RegisterDTO;
import util.Utils;

@Path("")
public class UserService {
	
	@Context
	private HttpServletRequest request;
	
	@Context
	private HttpServletResponse response;
	
	@Context
	private ServletContext context;
	
	/*
	 * User login
	 */
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User login(LoginDTO login){
		for(User user : getUsersRW().getUsers().values()){
			if(user.getUsername().equals(login.getUsername()) && user.getPassword().equals(login.getPassword())){
				request.getSession().setAttribute("user", user);
				return user;
			}
		}
		return null;
	}
	
	/*
	 * User logout
	 */
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(){
		request.getSession().invalidate();
		return Response.status(Status.OK).build();
	}
	
	/*
	 * Check for existing username
	 */
	@GET
	@Path("/exists")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response check(@QueryParam("username") String username){
		for(User user : getUsersRW().getUsers().values()){
			if(user.getUsername().equals(username)){
				return Response.status(Status.OK).entity("{\"exists\": true}").build();
			}
		}
		return Response.status(Status.OK).entity("{\"exists\": false}").build();
	}
	
	/*
	 * User registration
	 */
	@POST
	@Path("/register")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetails, 
			@FormDataParam("user") FormDataBodyPart jsonPart){
		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		RegisterDTO registerDTO = jsonPart.getValueAs(RegisterDTO.class);
		if(checkString(registerDTO.getUsername()) || checkString(registerDTO.getPassword()) || checkString(registerDTO.getFirstName()) || checkString(registerDTO.getLastName())
				|| checkString(registerDTO.getEmail()) || checkString(registerDTO.getPhone())){
			return Response.status(Status.BAD_REQUEST).build();
		}
		for(User user : getUsersRW().getUsers().values()){
			if(user.getUsername().equals(registerDTO.getUsername())){
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
		Territory t = getTerritoriesRW().getTerritories().get(registerDTO.getTerritory());
		if(t == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		User user = new User();
		user.setId(getUsersRW().getUsers().size() + 1);
		user.setAdmin(false);
		user.setBlocked(false);
		user.setUsername(registerDTO.getUsername());
		user.setPassword(registerDTO.getPassword());
		user.setFirstName(registerDTO.getFirstName());
		user.setLastName(registerDTO.getLastName());
		user.setPhone(registerDTO.getPhone());
		user.setEmail(registerDTO.getEmail());
		user.setTerritory(t);
		String fileName = fileDetails.getFileName();
		if(fileName != null && !fileName.trim().equals("")){
			long filestamp = (new Date()).getTime();
			fileName = filestamp + "_" + fileDetails.getFileName();
			user.setPicture(fileName);
			String uploadedFileLocation = Utils.getImagesFilePath() + fileName;
			Utils.saveToFile(uploadedInputStream, uploadedFileLocation);
		}
		else{
			user.setPicture("");
		}
		UsersRW usersRW = getUsersRW();
		usersRW.getUsers().put(user.getId(), user);
		usersRW.writeUsers(context.getRealPath(""));
		context.setAttribute("users", usersRW);
		return Response.status(Status.OK).build();
	}
	
	/*
	 * Get logged user
	 */
	@GET
	@Path("logged-user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User getLoggedUser(){
		return (User)request.getSession().getAttribute("user");
	}
	
	private UsersRW getUsersRW(){
		UsersRW users = (UsersRW)context.getAttribute("users");
		if(users == null){
			TerritoriesRW territories = (TerritoriesRW)context.getAttribute("territories");
			if(territories == null){
				territories = new TerritoriesRW();
				territories.readTerritories(context.getRealPath(""));
				context.setAttribute("territories", territories);
			}
			users = new UsersRW(territories.getTerritories());
			users.readUsers(context.getRealPath(""));
			context.setAttribute("users", users);
		}
		return users;
	}
	
	private TerritoriesRW getTerritoriesRW(){
		TerritoriesRW territories = (TerritoriesRW)context.getAttribute("territories");
		if(territories == null){
			territories = new TerritoriesRW();
			territories.readTerritories(context.getRealPath(""));
			context.setAttribute("territories", territories);
		}
		return territories;
	}
	
	private boolean checkString(String s){
		return s == null || s.trim().equals("");
	}
}
