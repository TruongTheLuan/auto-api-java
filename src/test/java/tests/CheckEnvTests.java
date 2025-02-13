package tests;

import org.junit.jupiter.api.Test;

import static utils.ConfigUtils.getDotEnv;

public class CheckEnvTests {
    @Test
    void checkEnv(){
        System.out.println(getDotEnv().get("API_HOST"));
    }
}
