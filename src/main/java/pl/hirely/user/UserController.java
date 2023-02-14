package pl.hirely.user;

public class UserController {

    private final UserServiceEither userService;

    public UserController(UserServiceEither userService) {
        this.userService = userService;
    }

    public Response getAllUsers() {
        return userService.getAllUsers()
                .map(users -> Response.builder()
                        .status(200)
                        .response(users.toString())
                        .build())
                .getOrElseGet(errorType -> Response.builder()
                        .status(mapErrorToHttpStatus(errorType))
                        .build());

    }

    private int mapErrorToHttpStatus(ErrorType errorType) {
        return switch (errorType) {
            case USERS_FETCH_ERROR -> 404;
            case INTERNAL_SERVER_ERROR -> 500;
        };
    }
}
