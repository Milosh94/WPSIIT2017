package services;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import beans.Comment;
import beans.EmergencySituation;
import beans.Territory;
import beans.User;
import dataRW.CommentsRW;
import dataRW.EmergencySituationsRW;
import dataRW.UsersRW;
import dto.EmergencySituationSimpleDTO;
import dto.ReportDTO;
import dto.SearchDTO;
import dto.UserCommentDTO;
import util.UrgentLevel;
import util.Utils;

@Path("")
public class EmergencySituationService {
	
	@Context
	private HttpServletRequest request;
	
	@Context
	private ServletContext context;
	
	/*
	 * Search emergency situations
	 */
	@GET
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@BeanParam SearchDTO search){
		User loggedUser = (User)context.getAttribute("user");
		if(loggedUser != null){
			if(loggedUser.isAdmin() == false){
				search.setWithoutVolunteer(false);
			}
		}
		else{
			search.setWithoutVolunteer(false);
		}
		List<EmergencySituation> result = new ArrayList<EmergencySituation>();
		for(EmergencySituation s : Utils.getEmergencySituationsRW(context).getEmergencySituations().values()){
			if(s.getStatus() != 1){
				continue;
			}
			if(search.isWithoutVolunteer() == true){
				if(s.getVolunteer() != null){
					continue;
				}
			}
			if(Utils.checkString(search.getEmergencySituationName()) == false){
				if(s.getName().toLowerCase().contains(search.getEmergencySituationName().toLowerCase()) == false){
					continue;
				}
			}
			if(Utils.checkString(search.getDistrictName()) == false){
				if(s.getDistrict().toLowerCase().contains(search.getDistrictName().toLowerCase()) == false){
					continue;
				}
			}
			if(Utils.checkString(search.getDescription()) == false){
				if(s.getDescription().toLowerCase().contains(search.getDescription().toLowerCase()) == false){
					continue;
				}
			}
			if(Utils.checkString(search.getVolunteer()) == false){
				if(s.getVolunteer().getUsername().toLowerCase().contains(search.getVolunteer().trim().toLowerCase()) == false){
					List<String> words = new ArrayList<String>(Arrays.asList(s.getVolunteer().getFirstName().split(" ")));
					words.addAll(Arrays.asList(s.getVolunteer().getLastName().split(" ")));
					List<String> searchWords = Arrays.asList(search.getVolunteer().trim().replaceAll("\\s+", " ").split(" "));
					boolean next = false;
					for(String searchWord : searchWords){
						boolean contains = false;
						for(int i = 0; i < words.size(); i++){
							if(words.get(i).toLowerCase().contains(searchWord.toLowerCase())){
								contains = true;
								words.remove(i);
								break;
							}
						}
						if(contains == false){
							next = true;
							break;
						}
					}
					if(next == true){
						continue;
					}
				}
			}
			if(search.getUrgencyLevels().size() != 0){
				boolean exists = false;
				for(String level : search.getUrgencyLevels()){
					if(level.equalsIgnoreCase(s.getUrgentLevel().toString())){
						exists = true;
						break;
					}
				}
				if(exists == false){
					continue;
				}
			}
			if(search.getTerritories().size() != 0){
				boolean exists = false;
				for(int territory : search.getTerritories()){
					if(s.getTerritory().getId() == territory){
						exists = true;
						break;
					}
				}
				if(exists == false){
					continue;
				}
			}
			result.add(s);
		}
		if(search.isNewest() == true){
			result = result.stream().sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime())).collect(Collectors.toList());
		}
		List<EmergencySituationSimpleDTO> resultTransformed = result.stream().map(s -> {
			return new EmergencySituationSimpleDTO(
					s.getId(), 
					s.getName(), 
					s.getDistrict(), 
					s.getDateTime(), 
					s.getTerritory().getName(), 
					s.getUrgentLevel().toString(), 
					s.getVolunteer() != null ? s.getVolunteer().getFirstName() + " " + s.getVolunteer().getLastName() : null, 
					s.getVolunteer() != null ? s.getVolunteer().getUsername() : null
			);
		}).collect(Collectors.toList());
		return Response.status(Status.OK).entity(resultTransformed).build();
	}
	
	/*
	 * Report situation
	 */
	@POST
	@Path("/report")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response report(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetails, 
			@FormDataParam("report") FormDataBodyPart jsonPart) throws UnsupportedEncodingException{
		jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		ReportDTO reportDTO = jsonPart.getValueAs(ReportDTO.class);
		if(Utils.checkString(reportDTO.getSituationName()) || Utils.checkString(reportDTO.getDistrict()) || Utils.checkString(reportDTO.getUrgencyLevel())
				|| Utils.checkReportFieldString(reportDTO.getDescription()) || Utils.checkReportFieldString(reportDTO.getLocation()) 
				|| Utils.checkReportFieldString(reportDTO.getLocationCoordinates()) || Utils.checkReportFieldString(reportDTO.getStreetNumber())){
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean exists = false;
		UrgentLevel urgencyLevel = null;
		for(int i = 0; i < UrgentLevel.values().length; i++){
			if(UrgentLevel.values()[i].toString().equalsIgnoreCase(reportDTO.getUrgencyLevel())){
				exists = true;
				urgencyLevel = UrgentLevel.values()[i];
				break;
			}
		}
		if(exists == false){
			return Response.status(Status.BAD_REQUEST).build();
		}
		Territory t = Utils.getTerritoriesRW(context).getTerritories().get(reportDTO.getTerritory());
		if(t == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		EmergencySituation situation = new EmergencySituation();
		situation.setId(Utils.getEmergencySituationsRW(context).getEmergencySituations().size() + 1);
		situation.setName(reportDTO.getSituationName().trim().replaceAll("\\s+", " "));
		situation.setDescription(reportDTO.getDescription().trim().replaceAll("\\s+", " "));
		situation.setDistrict(reportDTO.getDistrict().trim().replaceAll("\\s+", " "));
		situation.setLocation(reportDTO.getLocation().trim().replaceAll("\\s+", " "));
		situation.setLocationCoordinates(reportDTO.getLocationCoordinates());
		situation.setStreetNumber(reportDTO.getStreetNumber());
		situation.setStatus(0);
		situation.setUrgentLevel(urgencyLevel);
		situation.setDateTime(new Date());
		situation.setTerritory(t);
		situation.setVolunteer(null);
		String fileName = fileDetails.getFileName();
		if(fileName != null && !fileName.trim().equals("")){
			long filestamp = (new Date()).getTime();
			fileName = filestamp + "_" + fileDetails.getFileName();
			situation.setPicture(fileName);
			String uploadedFileLocation = Utils.getSituationsImagesFilePath(context.getRealPath("")) + fileName;
			Utils.saveToFile(uploadedInputStream, uploadedFileLocation);
		}
		else{
			situation.setPicture("");
		}
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		emergencySituationsRW.getEmergencySituations().put(situation.getId(), situation);
		emergencySituationsRW.writeEmergencySituations(context.getRealPath(""));
		context.setAttribute("situations", emergencySituationsRW);
		return Response.status(Status.OK).build();
	}
	
	/*
	 * list of emergency situations that are not published
	 */
	@GET
	@Path("/publish")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUnpublishedSituations(){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		List<EmergencySituationSimpleDTO> situations = Utils.getEmergencySituationsRW(context).getEmergencySituations().values()
				.stream()
				.filter(e -> e.getStatus() == 0)
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
		return Response.status(Status.OK).entity(situations).build();
	}
	
	/*
	 * Emergency situation with specific id information
	 */
	@GET
	@Path("emergency-situation/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmergencySituationById(@PathParam("id") int id){
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		EmergencySituation situation = emergencySituationsRW.getEmergencySituations().values().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
		if(situation == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		User user = (User)request.getSession().getAttribute("user");
		if(situation.getStatus() != 1 && (user == null || user.isBlocked())){
			return Response.status(Status.FORBIDDEN).build();
		}
		if(situation.getVolunteer() != null){
			User volunteer = new User();
			volunteer.setFirstName(situation.getVolunteer().getFirstName());
			volunteer.setLastName(situation.getVolunteer().getLastName());
			volunteer.setUsername(situation.getVolunteer().getUsername());
			situation.setVolunteer(volunteer);
		}
		CommentsRW commentsRW = Utils.getCommentsRW(context);
		List<Comment> comments = commentsRW.getComments().values()
				.stream()
				.filter(c -> c.getEmergencySituation().getId() == situation.getId())
				.map(c -> {
					Comment com = new Comment();
					com.setText(c.getText());
					com.setDateTime(c.getDateTime());
					com.setEmergencySituation(null);
					User u = new User();
					u.setUsername(c.getUser().getUsername());
					u.setFirstName(c.getUser().getFirstName());
					u.setLastName(c.getUser().getLastName());
					u.setPicture(c.getUser().getPicture());
					com.setUser(u);
					return com;
				})
				.sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
				.collect(Collectors.toList());
		situation.setComments(comments);
		if(user != null && situation.getStatus() != 1){
			if(user.isAdmin() == true){
				return Response.status(Status.OK).entity(situation).build();
			}
		}
		return Response.status(Status.OK).entity(situation).build();
	}
	
	/*
	 * Publish new emergency situation
	 */
	@PUT
	@Path("/publish/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response publishSituation(@PathParam("id") int id){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		EmergencySituation situation = emergencySituationsRW.getEmergencySituations().values().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
		if(situation == null || situation.getStatus() != 0){
			return Response.status(Status.BAD_REQUEST).build();
		}
		situation.setStatus(1);
		emergencySituationsRW.writeEmergencySituations(context.getRealPath(""));
		return Response.status(Status.OK).build();
	}
	
	/*
	 * Archive emergency situation
	 */
	@PUT
	@Path("/archive/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response archiveSituation(@PathParam("id") int id){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		EmergencySituation situation = emergencySituationsRW.getEmergencySituations().values().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
		if(situation == null || situation.getStatus() == -1){
			return Response.status(Status.BAD_REQUEST).build();
		}
		situation.setStatus(-1);
		emergencySituationsRW.writeEmergencySituations(context.getRealPath(""));
		return Response.status(Status.OK).build();
	}
	
	/*
	 * Change volunteer for emergency situation
	 */
	@PUT
	@Path("/emergency-situation/{id}/change-volunteer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeVolunteer(@PathParam("id") int id, @QueryParam("username") String username){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isAdmin() == false){
			return Response.status(Status.FORBIDDEN).build();
		}
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		EmergencySituation situation = emergencySituationsRW.getEmergencySituations().values().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
		if(situation == null || situation.getStatus() == -1){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(username != null && !username.trim().equals("")){
			UsersRW usersRW = Utils.getUsersRW(context);
			User volunteer = usersRW.getUsers().values().stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
			if(volunteer == null){
				return Response.status(Status.BAD_REQUEST).build();
			}
			else{
				if(situation.getTerritory().getId() != user.getTerritory().getId()){
					return Response.status(Status.BAD_REQUEST).build();
				}
				else{
					situation.setVolunteer(volunteer);
				}
			}
		}
		else{
			situation.setVolunteer(null);
		}
		emergencySituationsRW.writeEmergencySituations(context.getRealPath(""));
		return Response.status(Status.OK).build();
	}
	
	/*
	 * Registered user post comment to emergency situation
	 */
	@POST
	@Path("/emergency-situation/{id}/comment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postComment(@PathParam("id") int id, UserCommentDTO userComment){
		User user = (User)request.getSession().getAttribute("user");
		if(user == null || user.isBlocked() == true){
			return Response.status(Status.FORBIDDEN).build();
		}
		EmergencySituationsRW emergencySituationsRW = Utils.getEmergencySituationsRW(context);
		EmergencySituation situation = emergencySituationsRW.getEmergencySituations().values().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
		if(situation == null || situation.getStatus() != 1){
			return Response.status(Status.BAD_REQUEST).build();
		}
		if(userComment.getComment() == null || userComment.getComment().trim().equals("")){
			return Response.status(Status.BAD_REQUEST).build();
		}
		CommentsRW commentsRW = Utils.getCommentsRW(context);
		Comment comment = new Comment();
		int commentId = commentsRW.getComments().size() + 1;
		comment.setId(commentId);
		comment.setUser(user);
		comment.setEmergencySituation(situation);
		comment.setText(userComment.getComment());
		comment.setDateTime(new Date());
		commentsRW.getComments().put(commentId, comment);
		commentsRW.writeComment(comment, context.getRealPath(""));
		commentsRW.writeComments(context.getRealPath(""));
		return Response.status(Status.OK).build();
	}
}
