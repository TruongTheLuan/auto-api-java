package utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.platform.commons.util.StringUtils;

public class ConfigUtils {
    public static Dotenv dotenv;
    public static Dotenv getDotEnv(){
        String currentProfile = System.getenv("testProfile");
        if(currentProfile == null){
            currentProfile = "local";
        }
        if(dotenv == null){
            dotenv = Dotenv.configure()
                    .directory("configs")
                    .filename(String.format("%s.env", currentProfile))
                    .load();
        }
        return dotenv;
    }
}
