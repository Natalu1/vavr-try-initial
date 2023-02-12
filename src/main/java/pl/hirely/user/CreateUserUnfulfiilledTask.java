package pl.hirely.user;

public class CreateUserUnfulfiilledTask {
    UserDto userDto;
    Reason reason;

    public CreateUserUnfulfiilledTask(UserDto userDto, Reason reason) {
        this.userDto = userDto;
        this.reason = reason;
    }
}
