package services;

import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import dto.UserDTO;
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
	public UserDTO login(LoginDTO login){
		for(User user : getUsersRW().getUsers().values()){
			if(user.getUsername().trim().equals(login.getUsername()) && user.getPassword().trim().equals(login.getPassword())){
				request.getSession().setAttribute("user", user);
				UserDTO userDTO = new UserDTO(user.getUsername(), user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail(),
						user.getTerritory(), user.getPicture(), user.isBlocked(), user.isAdmin());
				return userDTO;
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
		if(checkString(registerDTO.getUsername()) || checkString(registerDTO.getFirstName()) || checkString(registerDTO.getLastName())
				|| checkString(registerDTO.getEmail()) || checkString(registerDTO.getPhone())){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(registerDTO.getPassword() == null || registerDTO.getPassword().equals("")){
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
	@Path("/logged-user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserDTO getLoggedUser(){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null){
			return null;
		}
		UserDTO userDTO = new UserDTO(user.getUsername(), user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail(),
				user.getTerritory(), user.getPicture(), user.isBlocked(), user.isAdmin());
		return userDTO;
	}
	
	/*
	 * Update user profile
	 */
	@PUT
	@Path("/user")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetails, 
			@FormDataParam("user") FormDataBodyPart jsonPart, @FormDataParam("oldPassword") String oldPassword, @FormDataParam("changePicture") boolean changePicture){
		User u = (User)request.getSession().getAttribute("user");
		if(u == null){
			return Response.status(Status.EXPECTATION_FAILED).build();
		}
		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		RegisterDTO registerDTO = jsonPart.getValueAs(RegisterDTO.class);
		if(checkString(registerDTO.getUsername()) || checkString(registerDTO.getFirstName()) || checkString(registerDTO.getLastName())
				|| checkString(registerDTO.getEmail()) || checkString(registerDTO.getPhone())){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(registerDTO.getPassword() == null || oldPassword == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(!u.getPassword().equals(oldPassword) && !oldPassword.equals("")){
			return Response.status(Status.BAD_REQUEST).entity("{\"wrongPassword\": true}").build();
		}
		boolean userExists = false;
		for(User user : getUsersRW().getUsers().values()){
			if(user.getUsername().equals(u.getUsername())){
				userExists = true;
				break;
			}
		}
		if(!userExists){
			return Response.status(Status.BAD_REQUEST).build();
		}
		Territory t = getTerritoriesRW().getTerritories().get(registerDTO.getTerritory());
		if(t == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(u.getPassword().equals(oldPassword) && registerDTO.getPassword().equals("")){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(u.getPassword().equals(oldPassword) && !registerDTO.getPassword().equals("")){
			u.setPassword(registerDTO.getPassword());
		}
		u.setEmail(registerDTO.getEmail());
		u.setFirstName(registerDTO.getFirstName());
		u.setLastName(registerDTO.getLastName());
		u.setPhone(registerDTO.getPhone());
		u.setTerritory(t);
		String fileName = fileDetails.getFileName();
		if(fileName != null && !fileName.trim().equals("") && changePicture == true){
			long filestamp = (new Date()).getTime();
			fileName = filestamp + "_" + fileDetails.getFileName();
			u.setPicture(fileName);
			String uploadedFileLocation = Utils.getImagesFilePath() + fileName;
			Utils.saveToFile(uploadedInputStream, uploadedFileLocation);
		}
		else{
			if(changePicture == true){
				u.setPicture("");
			}
		}
		UsersRW usersRW = getUsersRW();
		usersRW.getUsers().put(u.getId(), u);
		usersRW.writeUsers(context.getRealPath(""));
		context.setAttribute("users", usersRW);
		context.setAttribute("user", u);
		UserDTO userDTO = new UserDTO(u.getUsername(), u.getFirstName(), u.getLastName(), u.getPhone(), u.getEmail(),
				u.getTerritory(), u.getPicture(), u.isBlocked(), u.isAdmin());
		return Response.status(Status.OK).entity(userDTO).build();
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
