package model.login;

import lombok.*;
import model.user.UserAddressRequest;
import model.user.UserRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String username;
    private String password;
    public static LoginRequest getDefault(){
        return LoginRequest.builder()
                .username("staff")
                .password("1234567890")
                .build();
    }
}
