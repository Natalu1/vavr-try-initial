package pl.hirely.user;

import io.vavr.control.Option;
import pl.hirely.user.client.InternalServerErrorException;
import pl.hirely.user.client.NotFoundException;
import pl.hirely.user.client.UserClient;

import java.util.List;
import java.util.UUID;

public class UserServiceJava implements UserService {
    private final UserClient userClient;
    private final EmailSender emailSender;
    private final KafkaClient kafkaClient;

    public UserServiceJava(UserClient userClient, EmailSender emailSender, KafkaClient kafkaClient) {
        this.userClient = userClient;
        this.emailSender = emailSender;
        this.kafkaClient = kafkaClient;
    }

    @Override
    public List<UserDto> getAllUsers() {
        try {
            return userClient.findUsers();
        } catch (Exception e) {
            throw new UsersFetchException();
        }
    }

    @Override
    public List<String> getAllUserNames() {
        try {
            return userClient.findUsers().stream()
                    .map(UserDto::getName)
                    .toList();
        } catch (InternalServerErrorException | NotFoundException e) {
            throw new UsersFetchException();
        }
//        catch (NotFoundException e){
//            throw new UsersFetchException();
//        }


    }

    @Override
    public String getCommaSeparateUserNames() {
        try {
            List<String> listNames = userClient.findUsers().stream()
                    .map(UserDto::getName)
                    .toList();
            if (listNames.isEmpty()){
                return "No_users";
            }
                return String.join(", ", listNames);

        } catch (InternalServerErrorException | NotFoundException e) {
            throw new UsersFetchException();
        }
    }

    @Override
    public UserDto getUserByName(String name) {
        try {
            Option<UserDto> byName = userClient.findByName(name);
            if( byName.isEmpty()){
                return  byName.getOrElseThrow(()->new UsersFetchException());
            }
            return byName
                    .getOrElseThrow(()->new UsersFetchException());
        } catch (InternalServerErrorException | NotFoundException e) {
            throw new UsersFetchException();
        }
    }

    @Override
    public String getUserStatusByName(String name) {
        try {
            Option<UserDto> byName = userClient.findByName(name);
            if( byName.isDefined()){
                return "User found";
            }
            return String.format("User with name: %s does not exist", name);
        } catch (InternalServerErrorException e){
            return String.format("Server error while fetching user with %s", name);}
        catch (NotFoundException e){
            return String.format("Not found while fetching user with name: %s",name);
        }
    }

    @Override
    public boolean createUser(UserDto name) {
        try {
            UUID user = userClient.createUser(name);
            CreateUserUnfulfiilledTask task = new CreateUserUnfulfiilledTask(UserDto u, Reason r);
            if (user != null) {
                emailSender.send(String.format("User with id: %s created", name));
                return true;
            }
        } catch (InternalServerErrorException e) {
            kafkaClient.send(task);
        }
    }

//    @Override
//    public String getUserStatusByName(String name) {
//        try {
//            return userClient.findByName(name)
//                    .map(userDto -> "User found")
//                    .getOrElse(String.format("User with name: %s does not exist", name));
//        } catch (InternalServerErrorException e) {
//            return String.format("Server error while fetching user with name %s", name);
//        } catch (NotFoundException e) {
//            return String.format("Not found while fetching user with name: %s", name);
//        }
//    }


}



