package model.dto.login;

import lombok.*;

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
