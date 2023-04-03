package model;

import org.apache.commons.lang3.RandomStringUtils;
public class UserRandomGenerator {
    public static User random() {
        return new User(RandomStringUtils.randomAlphabetic(5) + "@ya.ru", "1111", "asdf");
    }
}
