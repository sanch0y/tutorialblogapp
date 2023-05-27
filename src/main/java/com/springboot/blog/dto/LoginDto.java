package com.springboot.blog.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto
{
    private String usernameOrEmail;
    private String password;
}
