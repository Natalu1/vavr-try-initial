package pl.hirely.user

import io.vavr.control.Option
import pl.hirely.user.client.BadRequestException
import pl.hirely.user.client.InternalServerErrorException
import pl.hirely.user.client.NotFoundException
import pl.hirely.user.client.UserClient
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate


class UserServiceJavaSpec extends Specification {

    private final UserClient userClient = Mock()
    private final EmailSender emailSender = Mock()
    private final KafkaClient kafkaClient = Mock()

    @Subject
//    private UserService userService = new UserServiceJava(userClient, emailSender, kafkaClient)
    private UserService userService = new UserServiceVavr(userClient, emailSender, kafkaClient)

    def "should return all users"() {


        given:
        userClient.findUsers() >> [
                new UserDto(name: "Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995, 01, 01))]
        when:
        def actual = userService.getAllUsers()

        then:
        actual.size() == 2
        actual*.name as Set == ["Anna", "Adam"] as Set
        actual*.birthDate as Set == [LocalDate.of(1988, 07, 21),
                                     LocalDate.of(1995, 01, 01)] as Set
    }

    def "should rethrow exception when client failed"() {
        given:
        userClient.findUsers() >> { throw clientException }
//        userClient.findUsers() >> {throw new InternalServerErrorException()}
        when:
        userService.getAllUsers()

        then:
        thrown(UsersFetchException)

        where:
        clientException << [new InternalServerErrorException(), new NotFoundException()]

    }

    def "should "() {
        given:
        userClient.findUsers() >> [
                new UserDto(name: "Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995, 01, 01))]

        when:
        def actual = userService.getAllUserNames()

        then:
        actual.size() == 2
        actual as Set == ["Anna", "Adam"] as Set

    }

    def "should rethrow exception when name failed"() {
        given:
        userClient.findUsers() >> { throw clientException }
//        userClient.findUsers() >> {throw new InternalServerErrorException()}
        when:
        userService.getAllUserNames()

        then:
        thrown(UsersFetchException)

        where:
//        clientException << [new InternalServerErrorException()]
        clientException << [new InternalServerErrorException(), new NotFoundException()]

    }

    def "should1"() {
        given:
        userClient.findUsers() >> []

        when:
        def actual = userService.getCommaSeparateUserNames()

        then:
        actual == "NO_USERS"
    }

    def "should getCommaSeparateUserNames "() {
        given:
        userClient.findUsers() >> [
                new UserDto(name: "Anna", birthDate: LocalDate.of(1988, 07, 21)),
                new UserDto(name: "Adam", birthDate: LocalDate.of(1995, 01, 01))]

        when:
        def actual = userService.getCommaSeparateUserNames()

        then:
        actual == "Anna, Adam"

    }

    def "should getUserDto"() {
        given:
        userClient.findByName("Anna") >> Option.of(new UserDto(name: "Anna"))

        when:
        def actual = userService.getUserByName("Anna")

        then:
        actual.name == "Anna"
    }

    def "should  exception when client failed"() {
        given:
        userClient.findByName(null) >> { throw clientException }
        when:
        userService.getUserByName(null)

        then:
        thrown(UsersFetchException)

        where:
        clientException << [new InternalServerErrorException(), new NotFoundException()]
    }

    def "should throw users fetch exception when client return empty option"() {
        given:
        userClient.findByName("Olga") >> Option.none()

        when:
        userService.getUserByName("Olga")

        then:
        thrown(UsersFetchException)
    }

    def "should get user status by name"() {
        given:
        userClient.findByName("Anna") >> Option.of(new UserDto(name: "Anna"))
        when:
        def actual = userService.getUserStatusByName("Anna")
        then:
        actual == "User found"
    }

    def "should get User status  when name  not found"() {
        given:
        userClient.findByName("Anna") >> Option.none()
        when:
        def actual = userService.getUserStatusByName("Anna")
        then:
        actual == "User with name: Anna does not exist"
    }

    def "should  exception when name failed"() {
        given:
        userClient.findByName("Anna") >> { throw new InternalServerErrorException() }
        when:
        userService.getUserStatusByName("Anna")
        then:
        "Server error while fetching user with Anna"
        where:
        clientException << new InternalServerErrorException()
    }

    def "should  exception when name exception"() {
        given:
        userClient.findByName("Anna") >> { throw new NotFoundException() }
        when:
        userService.getUserStatusByName("Anna")
        then:
        "Not found while fetching user with name: Anna"
        where:
        clientException << new NotFoundException()
    }

    def "should  exception when client failed1"() {
        given:
        userClient.findByName("Anna") >> { throw clientException }
        when:
        def actual = userService.getUserStatusByName("Anna")


        then:
        actual == "Server error while fetching user with Anna" || "Not found while fetching user with name: Anna"

        where:
        clientException << [new InternalServerErrorException(), new NotFoundException()]
    }

    def "should return exception message when client throw exception"() {
        given:
        userClient.findByName("Paul") >> { throw exceptionThrownByClient }

        when:
        def actual = userService.getUserStatusByName("Paul")

        then:
        actual == expectedResult

        where:
        exceptionThrownByClient            | expectedResult
        new InternalServerErrorException() | "Server error while fetching user with name: Paul"
        new NotFoundException()            | "Not found while fetching user with name: Paul"
    }


    def "param"() {
        given:
        userClient.findByName("Anna") >> data
        when:
        def actual = userService.getUserStatusByName("Anna")
        then:
        actual == expectedActual

        where:

        data                                 || expectedActual
        Option.of(new UserDto(name: "Anna")) || "User found"
        Option.none()                        || "User with name: Anna does not exist"
    }

    def "should create id user "() {
        given:
        def userUuid = UUID.randomUUID()
        def userDto = new UserDto(name: "Anna")
        userClient.createUser(userDto) >> userUuid
        when:
        def actual = userService.createUser(userDto)
        then:
        actual
        1 * emailSender.send(String.format("User with id: %s created", userUuid))
    }

    def "should return exception message"() {
        def userDto = new UserDto(name: "Anna")

        userClient.createUser(userDto) >> { throw clientException }

        when:
        def actual = userService.createUser(userDto)
        then:
        !actual
        1 * kafkaClient.send({ task ->
            task.getReason() == expectedResason
            task.userDto.name == userDto.name
        })
        0 * emailSender.send(_)

        where:
        expectedResason              | clientException
        Reason.INTERNAL_SERVER_ERROR | new InternalServerErrorException()
        Reason.NOT_FOUND             | new NotFoundException()
        Reason.BAD_REQUEST           | new BadRequestException()

    }


//    def "Name"(){
//        expect:
//        true
//    }
}
