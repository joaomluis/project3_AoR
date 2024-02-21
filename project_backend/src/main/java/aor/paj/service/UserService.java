package aor.paj.service;

import aor.paj.bean.TaskBean;
import aor.paj.bean.UserBean;
import aor.paj.dto.Task;
import aor.paj.dto.User;
import aor.paj.dto.UserDetails;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
public class UserService {

    @Inject
    UserBean userBean;

    @Inject
    TaskBean taskBean;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        return userBean.getUsers();
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsern(@PathParam("username")String username){
        UserDetails userRequested=userBean.getUserDetails(username);
        if (userRequested==null) return Response.status(400).entity("Failed").build();
        return Response.status(200).entity(userRequested).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUserRegister(@HeaderParam("username")String user_username, @HeaderParam("password")String user_password,
                                         @HeaderParam("email")String user_email, @HeaderParam("firstName")String user_firstName,
                                         @HeaderParam("lastName")String user_lastName,@HeaderParam("phoneNumber")String user_phoneNumber) {

        int validate = userBean.validateUserRegister(user_username,user_password,user_email,user_firstName,user_lastName,user_phoneNumber);

        if (validate==10) return Response.status(200).entity("New user was validated").build();

        else if(validate==4) return Response.status(400).entity("Phone number invalid").build();

        else if(validate==3) return Response.status(400).entity("Email invalid").build();

        else if(validate==2) return Response.status(409).entity("Email exists").build();

        else if(validate==1) return Response.status(409).entity("Username exists").build();

        else if(validate==0) return Response.status(400).entity("There are empty fields").build();

        return Response.status(405).entity("Something went wrong").build();

    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
            int validateUser = userBean.validateUserRegister(user.getUsername(), user.getPassword(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getPhoneNumber());
            if (validateUser == 10) {
                if (userBean.isValidUrl(user.getImgURL())) {
                    userBean.addUser(user);
                    return Response.status(200).entity("A new user was created").build();
                } else return Response.status(400).entity("The URL is invalid").build();
            } else if (validateUser == 4) return Response.status(400).entity("Phone number invalid").build();

            else if (validateUser == 3) return Response.status(400).entity("Email invalid").build();

            else if (validateUser == 2) return Response.status(409).entity("Email exists").build();

            else if (validateUser == 1) return Response.status(409).entity("Username exists").build();

            else if (validateUser == 0) return Response.status(400).entity("There are empty fields").build();

            return Response.status(405).entity("Something went wrong").build();



    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@HeaderParam("username")String username,@HeaderParam("pass")String pass){
        return userBean.getUser(username,pass);
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateLogin(@HeaderParam("username")String username, @HeaderParam("password")String password) {
        User user = userBean.validateLogin(username, password);
        if (user==null)
            return Response.status(404).entity("Failed").build();

        return Response.status(200).entity("Success").build();

    }

    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@QueryParam("username")String username) {
        boolean deleted = userBean.removeUser(username);
        if (!deleted) return Response.status(200).entity("User with this username is not found").build();

        return Response.status(200).entity("deleted").build();
    }
    @PUT
    @Path("/updatePhoto")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePhoto(@HeaderParam("username") String username, @HeaderParam("password")String password,@HeaderParam("newPhoto") String newPhoto){
        User updateUser = userBean.updatePhoto(username, password,newPhoto);
        if(updateUser != null){
            return Response.status(200).entity(updateUser).build();
        }else{
            return Response.status(404).entity("not found").build();
        }
    }
    @PUT
    @Path("/updatePassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePassword(@HeaderParam("username") String username, @HeaderParam("password") String password, @HeaderParam("newPassword") String newPassword){
        boolean fieldChanged = userBean.updatePassword(username, password, newPassword);
        if(fieldChanged){
            return Response.status(200).entity("Password changed with successfuly").build();
        }else{
            return Response.status(404).entity("not found").build();
        }

    }

    @PUT
    @Path("/updateEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmail(@HeaderParam("username") String username, @HeaderParam("password") String password, @HeaderParam("email") String email){
        boolean fieldChanged = userBean.updateEmail(username, password, email);
        if(fieldChanged){
            return Response.status(200).entity("Email changed with successfuly").build();
        }else{
            return Response.status(404).entity("not found").build();
        }
    }

    @PUT
    @Path("/updateFirstName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFirstName(@HeaderParam("username") String username, @HeaderParam("password") String password, @HeaderParam("firstName") String firstName){
        boolean fieldChanged = userBean.updateFirstName(username, password,firstName);
        if(fieldChanged){
            return Response.status(200).entity("First Name changed with successfuly").build();
        }else{
            return Response.status(404).entity("not found").build();
        }

    }
    @PUT
    @Path("/updateLastName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLastName(@HeaderParam("username") String username, @HeaderParam( "password") String password, @HeaderParam("lastName") String lastName){
           boolean fieldChanged = userBean.updateLastName(username, password, lastName);
            if(fieldChanged){
                return Response.status(200).entity("Last Name changed with successfuly").build();
            }else{
                return Response.status(404).entity("not found").build();
            }
    }

    @PUT
    @Path("/updatePhoneNumber")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePhoneNumber(@HeaderParam("username") String username, @HeaderParam("password") String password, @HeaderParam("phonenumber") String phonenumber){
        boolean fieldChanged = userBean.updatePhoneNumber(username, password, phonenumber);
        if(fieldChanged){
            return Response.status(200).entity("Phone Number  changed with successfuly").build();
        }else{
            return Response.status(404).entity("not found").build();
        }

    }



    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveColors(@HeaderParam("username")String username,@HeaderParam("pass")String password,@HeaderParam("background_color")String background_color,@HeaderParam("toDo_color")String toDo_color,
                                @HeaderParam("doing_color")String doing_color,@HeaderParam("done_color")String done_color){

        User userRequest=userBean.getUser(username,password);

        if (userRequest==null) return Response.status(404).entity("You don't have authorization to make changes").build();

        userBean.saveColors(userRequest, background_color,toDo_color,doing_color,done_color);
        return Response.status(200).entity("Colors were updated").build();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutValidate(@HeaderParam("username") String username, @HeaderParam("password")String pass){
        User userRequest=userBean.getUser(username,pass);

        if (userRequest==null) return Response.status(401).entity("Failed").build();

        return Response.status(200).entity("Success").build();
    }
}
