package pl.hirely.user;

import io.vavr.control.Try;
import pl.hirely.user.client.InternalServerErrorException;
import pl.hirely.user.client.UserClient;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceVavr implements UserService {

    private final UserClient userClient;

    public UserServiceVavr(UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return Try.of(userClient::findUsers)
                .getOrElseThrow(UsersFetchException::new);
    }

    @Override
    public List<String> getAllUserNames() {
        return Try.of(
                        () -> userClient.findUsers()
                                .stream()
                                .map(UserDto::getName)
                                .toList()
                )
                .getOrElseThrow(UsersFetchException::new);
    }

    @Override
    public String getCommaSeparateUserNames() {
        return Try.of(this::commaSeparatedUserNames)
                .getOrElseThrow(UsersFetchException::new);
    }


    private String commaSeparatedUserNames() throws InternalServerErrorException {
        List<UserDto> users = userClient.findUsers();
        if (users.isEmpty()) {
            return "NO_USERS";
        }
        return users.stream()
                .map(UserDto::getName)
                .collect(Collectors.joining(","));
    }


}
