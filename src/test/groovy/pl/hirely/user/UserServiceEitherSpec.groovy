package pl.hirely.user

import io.vavr.control.Either
import pl.hirely.user.client.InternalServerErrorException
import pl.hirely.user.client.NotFoundException
import pl.hirely.user.client.UserClient
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class UserServiceEitherSpec extends Specification {

    private final UserClient userClient = Mock()
    @Subject
    private UserServiceEither userServiceEither = new UserServiceEither(userClient)

    def "should return right either with users"() {

        given:
        userClient.findUsers() >>  [
                new UserDto(name: "Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995, 01, 01))]
        when:
        def actual = userServiceEither.getAllUsers()

        then:
        actual.isRight()
        actual.get()*.name == ["Anna", "Adam"]
    }

    def "should return left either with error type"() {

        given:
        userClient.findUsers() >> {throw clientException}
        when:
        def actual = userServiceEither.getAllUsers()


        then:
        actual.isLeft()
        actual.getLeft() == expectedErrorType

        where:
        clientException | expectedErrorType
        new NotFoundException() | ErrorType.USERS_FETCH_ERROR
        new InternalServerErrorException() | ErrorType.INTERNAL_SERVER_ERROR

    }



}
