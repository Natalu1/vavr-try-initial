package pl.hirely.user

import pl.hirely.user.client.InternalServerErrorException
import pl.hirely.user.client.NotFoundException
import pl.hirely.user.client.UserClient
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate


class UserServiceJavaSpec extends Specification {

    private UserClient userClient = Mock()

    @Subject
    private UserService userService = new UserServiceJava(userClient)

    def "should return all users"() {


        given:
        userClient.findUsers() >> [
                new UserDto (name:"Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995,01,01))]
        when:
        def actual = userService.getAllUsers()

        then:
        actual.size() == 2
        actual*.name as Set == ["Anna", "Adam"] as Set
        actual*.birthDate as Set == [LocalDate.of(1988, 07, 21),
                                     LocalDate.of(1995,01,01)] as Set
    }

    def "should rethrow exception when client failed"() {
        given:
        userClient.findUsers() >> {throw clientException}
//        userClient.findUsers() >> {throw new InternalServerErrorException()}
        when:
        userService.getAllUsers()

        then:
        thrown(UsersFetchException)

        where:
        clientException << [new InternalServerErrorException(), new NotFoundException()]

    }

    def "should "(){
        given:
        userClient.findUsers() >> [
                new UserDto (name:"Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995,01,01))]
        when:
        userService.getAllUserNames()


    }


//    def "Name"(){
//        expect:
//        true
//    }
}
