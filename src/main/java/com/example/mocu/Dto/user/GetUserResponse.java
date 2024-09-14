package com.example.mocu.Dto.user;

import com.example.mocu.domain.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetUserResponse {
    private Long userId;
    private String name;
    private String email;
    private String userImage;
    private String status;
    private String oAuthProvider;

    public GetUserResponse(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.userImage = user.getUserImage();
        this.status = user.getStatus();
        this.oAuthProvider = user.getProvider();
    }
}
