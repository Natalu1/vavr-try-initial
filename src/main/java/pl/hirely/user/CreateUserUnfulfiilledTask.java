package pl.hirely.user;

public class CreateUserUnfulfiilledTask {
    UserDto userDto;
    Reason reason;

    public CreateUserUnfulfiilledTask(UserDto userDto, Reason reason) {
        this.userDto = userDto;
        this.reason = reason;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public Reason getReason() {
        return reason;
    }

    public static CreateUserUnfulfiilledTask notFound(UserDto user) {
        return new CreateUserUnfulfiilledTask (user,Reason.NOT_FOUND);
    }

    public static CreateUserUnfulfiilledTask badRequest(UserDto user) {
        return new CreateUserUnfulfiilledTask(user,Reason.BAD_REQUEST);
    }

    public static CreateUserUnfulfiilledTask internalServerError(UserDto user) {
        return new CreateUserUnfulfiilledTask(user,Reason.INTERNAL_SERVER_ERROR);
    }
}
