package aor.paj.bean;

import aor.paj.dto.Task;
import aor.paj.dto.User;
import aor.paj.dto.UserDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

@ApplicationScoped
public class UserBean {

    final String filename = "users.json";
    private ArrayList<User> users;

    public UserBean(){
        File f = new File(filename);
        if(f.exists()){
            try {
                FileReader filereader = new FileReader(f);
                users = JsonbBuilder.create().fromJson(filereader, new
                        ArrayList<User>() {}.getClass().getGenericSuperclass());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }else
            users = new ArrayList<User>();

    }
    //Método para adicionar um user novo ao json
    public void addUser(User user) {
        users.add(user);
        writeIntoJsonFile();
    }

    //Método para adicionar uma task nova a um user
    public void addTask(User user, Task task){
        user.getTasks().add(task);
        writeIntoJsonFile();
    }
    //Método para eliminar uma task
    public boolean removeTask(User user,long id) {
        boolean taskRemoved=false;
        ArrayList<Task> tasksRequested=user.getTasks();
        for (int i=0;i<tasksRequested.size() && !taskRemoved;i++) {
            if (tasksRequested.get(i).getId() == id) {
                tasksRequested.remove(i);
                taskRemoved=true;
            }
        }
        writeIntoJsonFile();
        return taskRemoved;
    }

    //getter de uma task a partir do seu id
    public Task getTask(User user, long id){
        Task taskRequested=null;
        ArrayList<Task> tasksUser=user.getTasks();
        System.out.println(user.getUsername());
        for (int i=0;i<tasksUser.size() && taskRequested==null;i++){

            if (tasksUser.get(i).getId()==id){
                taskRequested=tasksUser.get(i);
            }
        }
        System.out.println(taskRequested);
        return taskRequested;
    }

    //faz o update do estado da task que recebe como input
    public void updateTaskState(Task task, String state){
        task.changeState(state);
        writeIntoJsonFile();
    }

    //faz o update dos atributos da task que recebe como input
    public void updateTask(Task task, String title, String description, LocalDate initialDate, LocalDate endDate,
                           int priority){
        task.setTitle(title);
        task.setDescription(description);
        task.setInitialDate(initialDate);
        task.setEndDate(endDate);
        task.setPriority(priority);
        writeIntoJsonFile();
    }

    //Método em que o output é o objeto UserDetails que tem todos os atributos iguais ao User menos a pass
    public UserDetails getUserDetails(String username){
        User userRequested=null;
        UserDetails userDetails=null;
        for(int i=0;i<users.size() && userRequested==null;i++){
            if(users.get(i).getUsername().equals(username)){
                userRequested=users.get(i);
            }
        }

        if(userRequested!=null) {

            userDetails = new UserDetails(userRequested.getUsername(), userRequested.getEmail(), userRequested.getFirstName(),
                    userRequested.getLastName(), userRequested.getImgURL(), userRequested.getPhoneNumber());
        }
        return userDetails;
    }
    //getter do user a partir do seu username e da sua password
    public User getUser(String username, String password){
        User userRequested=null;
        for(int i=0;i<users.size() && userRequested==null;i++){
            if(users.get(i).getUsername().equals(username) && users.get(i).getPassword().equals(password)){
                userRequested=users.get(i);
            }
        }
        return userRequested;
    }
    //método para validar um user novo e dependendo da verificação em que falhar manda uma resposta diferente
    public int validateUserRegister(String username,String password, String email, String firstName, String lastName, String phoneNumber){

        final int EMPTY_FIELDS=0, USERNAME_EXISTS=1, EMAIL_EXISTS=2,INVALID_EMAIL=3,INVALID_PHONE=4,USER_VALIDATE=10;
        int VALIDATION_STATE=USER_VALIDATE;

        if(username.equals("") || password.equals("") || email.equals("") || firstName.equals("") || lastName.equals("") || phoneNumber.equals("")) {

            VALIDATION_STATE= EMPTY_FIELDS;
        }
        else if(!isValidEmail(email)){
            VALIDATION_STATE=INVALID_EMAIL;
        }
        else if (!isValidPhoneNumber(phoneNumber)){
            VALIDATION_STATE=INVALID_PHONE;
        }
        else{
            for (int i=0;i<users.size() && VALIDATION_STATE==USER_VALIDATE;i++) {

                if (users.get(i).getUsername().equals(username)){
                    VALIDATION_STATE= USERNAME_EXISTS;
                }
                else if (users.get(i).getEmail().equals(email)){
                    VALIDATION_STATE= EMAIL_EXISTS;
                }
            }
        }
        return VALIDATION_STATE;
    }

    //Recebe uma string e vê se é um número de telefone válido
    public boolean isValidPhoneNumber(String phoneNumber){
        boolean valideNumber=false;
        try {

            String cleanedPhoneNumber = phoneNumber.replaceAll("[^\\d]", "");

            if (cleanedPhoneNumber.length() == 9 || cleanedPhoneNumber.length() == 10) {
                valideNumber=true;
            } else {
                valideNumber= false;
            }
        } catch (NumberFormatException e) {
            valideNumber=false;
        }
        return valideNumber;
    }

    //verifica se um URL é válido
    public boolean isValidUrl(String urlString) {
        try {

            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {

            return false;
        }
    }

    //verifica se um email é válido
    public boolean isValidEmail(String email) {
        boolean isValid = false;
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
        }
        return isValid;
    }

    public void saveColors(User user,String background_color,String toDo_color, String doing_color, String done_color){
        user.changeBackground_color(background_color);
        user.changeToDo_color(toDo_color);
        user.changeDoing_color(doing_color);
        user.changeDone_color(done_color);
        writeIntoJsonFile();
    }

    public User validateLogin(String username, String password) {
        User user_validate=null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if(user.getPassword().equals(password)) user_validate=user;
            }
        }
        return user_validate;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public boolean removeUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                users.remove(user);
                return true;
            }
        }
        return false;
    }


    public User updatePhoto(String username,String pass,String newPhoto){
        User currentUser = getUser(username,pass);
        currentUser.setImgURL(newPhoto);
        writeIntoJsonFile();

        return currentUser;
    }
    public boolean updatePassword(String username, String password, String newPassword) {
        boolean fieldChanged = false;
            User u = getUser(username, password);
            if(u!=null) {
                u.setPassword(newPassword);
                writeIntoJsonFile();
                fieldChanged = true;
            }

        return fieldChanged;
    }
    public boolean updateEmail(String username, String password, String email) {
        boolean fieldChanged = false;
        boolean validEmail = isValidEmail(email);
            User u = getUser(username, password);
            boolean emailAlreadyExists = emailExists(email);
            if (u !=null && validEmail && !emailAlreadyExists) {
                    u.setEmail(email);
                    writeIntoJsonFile();
                    fieldChanged = true;
        }
        return fieldChanged;
    }

    public boolean emailExists(String email){
        boolean emailExists = false;
            for (User u : users) {
                String userEmail = u.getEmail();
                if (userEmail != null && userEmail.equals(email)) {
                    emailExists = true;
                }
            }
        return emailExists;
    }

    public boolean updateFirstName(String username, String password, String firstName) {
        boolean fieldChanged = false;

            User u = getUser(username, password);
            if(u!=null){
                u.setFirstName(firstName);
                writeIntoJsonFile();
                fieldChanged=true;
        }
        return fieldChanged;
    }
    public boolean updateLastName(String username, String password, String lastName) {
        boolean fieldChanged = false;
            User u = getUser(username, password);
            if(u!= null){
                u.setLastName(lastName);
                writeIntoJsonFile();
                fieldChanged=true;
        }
        return fieldChanged;
    }

    public boolean updatePhoneNumber(String username, String password, String phoneNumber) {
        boolean fieldChanged = false;
            User u = getUser(username, password);

            boolean phoneValid=isValidPhoneNumber(phoneNumber);
            if (u!=null && phoneValid) {
                u.setPhoneNumber(phoneNumber);
                writeIntoJsonFile();
                fieldChanged = true;
        }
        return fieldChanged;
    }

    public boolean phoneExists(String phoneNumber){
        boolean phoneExists = false;
        for(User u: users){
            if(u.getPhoneNumber().equals(phoneNumber)){
                phoneExists = true;
            }
        }
        return phoneExists;
    }


    private void writeIntoJsonFile(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            JsonbConfig config = new JsonbConfig().withFormatting(true);
            Jsonb jsonb = JsonbBuilder.create(config);
            jsonb.toJson(users, fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
       /* Jsonb jsonb = JsonbBuilder.create(new
                JsonbConfig().withFormatting(true));
        try {
            jsonb.toJson(users, new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }*/
    }
}
