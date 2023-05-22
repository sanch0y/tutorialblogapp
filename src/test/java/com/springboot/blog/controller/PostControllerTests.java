package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.dto.LoginDto;
import com.springboot.blog.dto.PostDto;
import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtAuthenticationFilter;
import com.springboot.blog.security.JwtTokenProvider;
import com.springboot.blog.service.CategoryService;
import com.springboot.blog.service.PostService;
import com.springboot.blog.service.impl.AuthServiceImpl;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.*;

import static org.mockito.BDDMockito.given;

//@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureMockMvc
@WebMvcTest(controllers = PostController.class) //Only load the required controller. Avoid loading the service and repository controller.
public class PostControllerTests
{
    @Autowired
    private MockMvc mockMvc; //To call REST APIs.

    @MockBean  //Tells Spring to create this Mock instance of PostService and add it to the application context so that it is injected into EmployeeController.
    private PostService postService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private AuthServiceImpl authServiceImpl;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private Post postEntity;

    private PostDto requestDto;

    private PostDto responseDto;

    private Category category;

    private LoginDto loginDto;

    private Role adminRole;

//    @MockBean
//    private Role userRole;

    @MockBean
    private User user;

    private static final String SECRET = "103e3e7b464113b1722f0af3dc4e8ad043a0efdc7e86b9a2e7e7c848069f7714";
    private static final long EXPIRATION_MS = 2592000000L;

    @BeforeEach
    public void setPostWIthCategory()
    {
        category = Category.builder()
                .id(1)
                .name("category1")
                .description("description1")
                .build();

        requestDto = PostDto.builder()
                .title("PostTitle1")
                .description("PostDescription1")
                .content("PostContent1")
                .categoryId(category.getId())
                .build();

        postEntity = Post.builder()
                .id(1L)
                .title("PostTitle1")
                .description("PostDescription1")
                .content("PostContent1")
                .category(category)
                .build();

        responseDto = PostDto.builder()
                .id(1)
                .title("PostTitle1")
                .description("PostDescription1")
                .content("PostContent1")
                .categoryId(postEntity.getCategory().getId())
                .build();

        loginDto.setUsernameOrEmail("sanchoy@gmail.com");
        loginDto.setPassword("admin123");

        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);

        user.setId(1L);
        user.setName("admin");
        user.setUsername("admin");
        user.setEmail("sanchoy@gmail.com");
        user.setPassword("admin123");
        user.setRoles(adminRoles);
    }

    @DisplayName("Controller_Logic_01")
    @Test //1//BDD//
    public void givenPostDtoObject_whenSavePost_thenReturn403Forbidden() throws Exception
    {
        //given - precondition or setup
        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testCreatePost() throws Exception {

//        Date now = new Date();
//        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
//
//        String jwtToken = "Bearer "+ Jwts.builder()
//                .setSubject("admin@gmail.com")
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + EXPIRATION_MS))
//                .signWith(key)
//                .compact();
//
//        given(jwtTokenProvider.validateToken(jwtToken)).willReturn(true);
//        given(jwtTokenProvider.getUsername(jwtToken)).willReturn("admin@gmail.com");
//
//        UserDetails userDetails = User
//                .withUsername("admin@gmail.com")
//                .password("admin123")
//                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
//                .build();
//        given(userDetailsService.loadUserByUsername("admin@gmail.com")).willReturn(userDetails);
//
//        authenticationToken = new UsernamePasswordAuthenticationToken(
//                userDetails,
//                null,
//                userDetails.getAuthorities()
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //given - precondition or setup

//        given(authServiceImpl.login(ArgumentMatchers.any(LoginDto.class)))
//                .willReturn(Optional.of(user));

        given(userRepository.findByUsernameOrEmail(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class)))
                .willReturn(Optional.of(user));


        String jwtToken = "Bearer " + authServiceImpl.login(loginDto);

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

}
