package pl.hirely.user;

import io.vavr.control.Try;
import pl.hirely.user.client.InternalServerErrorException;
import pl.hirely.user.client.NotFoundException;
import pl.hirely.user.client.UserClient;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceJava implements UserService {
    private final UserClient userClient;

    public UserServiceJava(UserClient userClient) {
        this.userClient = userClient;
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
                return String.join(",", listNames);

        } catch (InternalServerErrorException | NotFoundException e) {
            throw new UsersFetchException();
        }

    }
}



