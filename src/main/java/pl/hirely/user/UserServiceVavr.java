package pl.hirely.user;

import io.vavr.control.Try;
import pl.hirely.user.client.BadRequestException;
import pl.hirely.user.client.InternalServerErrorException;
import pl.hirely.user.client.NotFoundException;
import pl.hirely.user.client.UserClient;

import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class UserServiceVavr implements UserService {

    private final UserClient userClient;
    private final EmailSender emailSender;
    private final KafkaClient kafkaClient;

    public UserServiceVavr(UserClient userClient, EmailSender emailSender, KafkaClient kafkaClient) {
        this.userClient = userClient;
        this.emailSender = emailSender;
        this.kafkaClient = kafkaClient;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return Try.of(userClient::findUsers)
                .getOrElseThrow(UsersFetchException::new);
    }

    @Override
    public List<String> getAllUserNames() {
        return Try.of(userClient::findUsers)
                .map(userDtos -> userDtos.stream()
                        .map(UserDto::getName)
                        .toList())


//                        () -> userClient.findUsers()
//                                .stream()
//                                .map(UserDto::getName)
//                                .toList()

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
                .collect(Collectors.joining(", "));
    }

    @Override
    public UserDto getUserByName(String name) {
        return Try.of(() -> userClient.findByName(name))
                .map(user -> user.get())
                .getOrElseThrow(UsersFetchException::new);

//        return Try.of( () -> userClient.findByName(name)
//                .getOrElseThrow(UsersFetchException::new)
//        ).getOrElseThrow(UsersFetchException::new);
    }

    @Override
    public String getUserStatusByName(String name) {
        return Try.of(() -> userClient.findByName(name))
                .map(user -> user.get())
                .map(user -> "User found")
                .recover(InternalServerErrorException.class, String.format("Server error while fetching user with name %s", name))
                .recover(NotFoundException.class, String.format("Not found while fetching user with name: %s", name))
//On match-case
//                .recover(exception -> Match(exception).of(
//                        Case($(instanceOf(InternalServerErrorException.class)), String.format("Server error while fetching user with name %s", name)),
//                        Case($(instanceOf(NotFoundException.class)), String.format("Not found while fetching user with name: %s", name))
//                ))
                .getOrElse(String.format("User with name: %s does not exist", name));





    }

    @Override
    public boolean createUser(UserDto userDto) {
        return Try.of(() -> userClient.createUser(userDto))
                .onSuccess(userId -> emailSender.send(String.format("User with id: %s created", userId)))
                .onFailure(exception -> kafkaClient.send(Match(exception).of(
                        Case($(instanceOf(BadRequestException.class)), CreateUserUnfulfiilledTask.badRequest(userDto)),
                        Case($(instanceOf(NotFoundException.class)), CreateUserUnfulfiilledTask.notFound(userDto)),
                        Case($(instanceOf(InternalServerErrorException.class)), CreateUserUnfulfiilledTask.internalServerError(userDto))
                )))
                .isSuccess();
    }

}
