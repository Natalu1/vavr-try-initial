package pl.hirely.user;

import java.time.LocalDate;

public class UserDto {

    private String name;

    private LocalDate birthDate;

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
}
