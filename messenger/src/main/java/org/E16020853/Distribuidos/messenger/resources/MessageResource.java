package org.E16020853.Distribuidos.messenger.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.E16020853.Distribuidos.messenger.model.Message;
import org.E16020853.Distribuidos.messenger.resources.beans.MessageFilterBean;
import org.E16020853.Distribuidos.messenger.service.MessageService;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/messages") //Ruta que tendrá dentro del "Navegador"
@Consumes(MediaType.APPLICATION_JSON) //Administrador de JSON
@Produces(value = {MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
public class MessageResource {

	MessageService messageService = new MessageService();
	//Método que filtra los mensajes por año
	@GET
	public List<Message> getMessages(@BeanParam MessageFilterBean filterBean){
		if (filterBean.getYear() > 0) {
			return messageService.getAllMessagesForYear(filterBean.getYear());
		}
		if (filterBean.getStart() >= 0 && filterBean.getSize() > 0) {
			return messageService.getAllMessagesPaginated(filterBean.getStart(),
														   filterBean.getSize());
		}
		return messageService.getAllMessages();
	}
	
	//Metodo que permite agregar un mensaje
	@POST
	public Response addMessage(Message message, @Context UriInfo uriInfo) throws URISyntaxException{		
		Message newMessage = messageService.addMessage(message);
		String newId = String.valueOf(newMessage.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri)
				.entity(newMessage)
				.build();
	}
	
	//Método que permite modificar un mensaje que ya exista, el cual sera localizado por medio de si Id = identificador
	@PUT
	@Path("/{messageId}")
	public Message updateMessage(@PathParam("messageId") long  id, Message message) {
		message.setId(id);
		return messageService.updateMessage(message);
	}
	
	//Método que permite eliminar un mensaje, para el cual se tiene que conocer si Id para poder realizarlo
	@DELETE
	@Path("/{messageId}")
	public void deleteMessage(@PathParam("messageId") long  id) {
		messageService.removeMessage(id);
	}
	
	
	//Método que permite obtener todos los mensajes que ya existan, lo que se va a mostrar será el mensaje, el nombre del autor y si existe
	//algun comentario tambien lo hará
	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long  id, @Context UriInfo uriInfo) {
		Message message = messageService.getMessage(id);
		message.addLink(getUriForSelf(uriInfo, message), " Self");
		message.addLink(getUriForProfile(uriInfo, message), " Profile");
		message.addLink(getUriForComments(uriInfo, message), " Comments");	
		
		return message;
	}

	private String getUriForComments(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
				.path(MessageResource.class, "getCommentResource")
				.path(CommentResource.class)
				.resolveTemplate("messageId", message.getId())
				.build();
		return uri.toString();
	}

	private String getUriForProfile(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
				.path(ProfileResource.class)
				.path(message.getAuthor())
				.build();
		return uri.toString();
	}

	private String getUriForSelf(UriInfo uriInfo, Message message) {
		String uri = uriInfo.getBaseUriBuilder()
		.path(MessageResource.class)
		.path(Long.toString(message.getId()))
		.build()
		.toString();
		return uri;
	}
	
	
	
	@GET
	@Path("/{messageId}/comments")
	public CommentResource getCommentResource() {
		return new CommentResource();
	}
	
}
