package pl.hirely.user;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    List<String> getAllUserNames();

    String getCommaSeparateUserNames();

    UserDto getUserByName(String name);

    String getUserStatusByName(String name);



}
