package pl.hirely.user;

import pl.hirely.user.client.InternalServerErrorException;
import pl.hirely.user.client.UserClient;

import java.util.List;

public class UserServiceJava implements UserService{
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
        } catch (InternalServerErrorException e) {
            throw new UsersFetchException();
        }
    }
}
