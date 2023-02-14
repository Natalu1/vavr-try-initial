package pl.hirely.user

import io.vavr.control.Either
import pl.hirely.user.client.UserClient
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class UserControllerSpec extends Specification {


    private UserServiceEither userServiceEither = Mock()
    @Subject
    private UserController userController = new UserController(userServiceEither)

    def "should return status 200 and users"() {
        given:
        userServiceEither.getAllUsers() >> Either.right([
                new UserDto(name: "Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995, 01, 01))])
        when:
        def actual = userController.getAllUsers()

        then:
        actual.httpStatus == 200
        actual.response == '[name=Anna, name=Adam]'
    }

    def "should return correct status code  with empty response"() {
        given:
        userServiceEither.getAllUsers() >> Either.left(errorType)
        when:
        def actual = userController.getAllUsers()

        then:
        actual.httpStatus == expectedStatusCode
        actual.response == null

        where:
        errorType                       | expectedStatusCode
        ErrorType.INTERNAL_SERVER_ERROR |   500
        ErrorType.USERS_FETCH_ERROR     |   404
    }


}
