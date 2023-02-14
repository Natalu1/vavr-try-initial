package pl.hirely.user;
import io.vavr.control.Either;
import io.vavr.control.Try;
import pl.hirely.user.client.InternalServerErrorException;
import pl.hirely.user.client.NotFoundException;
import pl.hirely.user.client.UserClient;

import java.util.List;



public class UserServiceEither {
    private final UserClient userClient;

    public UserServiceEither(UserClient userClient) {
        this.userClient = userClient;
    }

    public Either<ErrorType, List<UserDto>> getAllUsers() {
        return Try.of(userClient::findUsers)
                .map(users -> success(users))
                .recover(NotFoundException.class, Either.left(ErrorType.USERS_FETCH_ERROR))
                .recover(InternalServerErrorException.class, Either.left(ErrorType.INTERNAL_SERVER_ERROR))
                .get();
    }

    private Either<ErrorType, List<UserDto>> success(List<UserDto> users) {
        return Either.right(users);
    }

}
