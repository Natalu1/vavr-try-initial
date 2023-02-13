package pl.hirely.user

import io.vavr.control.Option
import pl.hirely.user.client.InternalServerErrorException
import pl.hirely.user.client.NotFoundException
import pl.hirely.user.client.UserClient
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class UserServiceVavrSpec extends Specification {
    private UserClient userClient = Mock()

    @Subject
    private UserService userService = new UserServiceVavr(userClient, emailSender, kafkaClient)

    def "should getCommaSeparateUserNames "(){
        given:
        userClient.findUsers() >> [
                new UserDto (name:"Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995,01,01))]

        when:
        def actual = userService.getCommaSeparateUserNames()

        then:
        actual  == "Anna,Adam"

    }

    def "should rethrow exception when name failed"() {
        given:
        userClient.findUsers() >> {throw clientException}
        when:
        userService.getAllUserNames()

        then:
        thrown(UsersFetchException)

        where:
        clientException << [new InternalServerErrorException(), new NotFoundException()]
    }
    def "should getUserDto null"(){
        given:
        userClient.findByName("Olga") >> Option.none()

        when:
        def actual = userService.getUserByName("Olga")

        then:
        thrown(UsersFetchException)
    }
}
