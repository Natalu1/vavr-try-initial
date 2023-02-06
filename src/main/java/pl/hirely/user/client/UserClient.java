package pl.hirely.user.client;

import io.vavr.control.Option;
import pl.hirely.user.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserClient {

    List<UserDto> findUsers() throws NotFoundException, InternalServerErrorException;

    Option<UserDto> findByName(String fullName) throws NotFoundException, InternalServerErrorException;

    UUID createUser(UserDto userDto) throws BadRequestException, InternalServerErrorException, NotFoundException;

}
