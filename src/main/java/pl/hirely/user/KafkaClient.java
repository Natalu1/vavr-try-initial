package pl.hirely.user;

public interface KafkaClient {

    void send (CreateUserUnfulfiilledTask task);
}
