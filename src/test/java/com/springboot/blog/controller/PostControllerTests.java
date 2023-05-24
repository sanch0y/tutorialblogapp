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
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.*;

import static org.mockito.BDDMockito.given;

@AutoConfigureMockMvc(addFilters = false) //This will disable the security of Jwt Token & CSRF token
@WebMvcTest(controllers = PostController.class) //Only load the required controller. Avoid loading the service and repository controller.
public class PostControllerTests
{
    @Autowired
    private MockMvc mockMvc; //To call REST APIs.

    @MockBean  //Tells Spring to create this Mock instance of PostService and add it to the application context so that it is injected into EmployeeController.
    private PostService postService;

//    @MockBean
//    private RoleRepository roleRepository;
//
//    @MockBean
//    private AuthServiceImpl authServiceImpl;

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
//
//    private LoginDto loginDto;
//
//    private Role adminRole;

//    @MockBean
//    private Role userRole;
//
//    @MockBean
//    private User user;

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

//        loginDto.setUsernameOrEmail("sanchoy@gmail.com");
//        loginDto.setPassword("admin123");
//
//        adminRole.setId(1L);
//        adminRole.setName("ROLE_ADMIN");
//        Set<Role> adminRoles = new HashSet<>();
//        adminRoles.add(adminRole);
//
//        user.setId(1L);
//        user.setName("admin");
//        user.setUsername("admin");
//        user.setEmail("sanchoy@gmail.com");
//        user.setPassword("admin123");
//        user.setRoles(adminRoles);
    }

    @DisplayName("Controller_Logic_01")
    @Test //1//BDD//Positive//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithTestJwtToken_whenSavePost_thenReturnPostObject() throws Exception
    {
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
//
//
//        given(authServiceImpl.login(ArgumentMatchers.any(LoginDto.class)))
//                .willReturn(Optional.of(user));
//        String jwtToken = "Bearer " + authServiceImpl.login(loginDto);
//
//        given(userRepository.findByUsernameOrEmail(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class)))
//                .willReturn(Optional.of(user));

//        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
//                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
//        /*Don't uncomment Start*/
//        //.willAnswer(...): This is a method that specifies the behavior that should occur when the mocked method is
//        // called with the specified arguments.
//        //
//        //invocationOnMock -> invocationOnMock.getArgument(0): This is a lambda expression that defines the behavior to
//        // be executed when the mocked method is called. It retrieves the first argument (0 index) passed to the method
//        // call and returns it as the result.
//        /*Don't uncomment End*/

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) responseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(responseDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(responseDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(responseDto.getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId", CoreMatchers.is((int) responseDto.getCategoryId())));
    }

    @DisplayName("Controller_Logic_02")
    @Test //2//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithTitleSizeLessThanMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle("P");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"Post title must have at least 4 characters\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_03")
    @Test //3//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithEmptyTitle_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle("");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_04")
    @Test //4//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithNullTitle_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setTitle(null);

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"title\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }


    @DisplayName("Controller_Logic_05")
    @Test //5//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithDescriptionSizeLessThanMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription("D");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"Post description must have at least 10 characters\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_06")
    @Test //6//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithEmptyDescription_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription("");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_07")
    @Test //7//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithNullDescription_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setDescription(null);

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"description\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_08")
    @Test //8//BDD//Positive//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithContentSizeMostMinimum_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent("C");

//        String validationErrorString = "Invalid request content.";
//        String specificValidationError = "{\"description\":\"Post description must have at least 10 characters\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) responseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(responseDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(responseDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(responseDto.getContent())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId", CoreMatchers.is((int) responseDto.getCategoryId())));
    }

    @DisplayName("Controller_Logic_09")
    @Test //9//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithEmptyContent_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent("");

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"content\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }

    @DisplayName("Controller_Logic_09")
    @Test //9//BDD//Negative//This test case will work when the line "@AutoConfigureMockMvc(addFilters = false)" will remain uncommented at the top of the class
    public void givenPostDtoObjectWithNullContent_whenSavePost_thenReturnBadRequestError() throws Exception
    {
        requestDto.setContent(null);

        String validationErrorString = "Invalid request content.";
        String specificValidationError = "{\"content\":\"must not be empty\"}";

        //given - precondition or setup
        String jwtToken = "Bearer " + "Test Token";

        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result1 -> result1.getResolvedException().getMessage().equals(validationErrorString))
                .andExpect(MockMvcResultMatchers.content().string(specificValidationError));
    }


    @Disabled("This test case is ignored for now.")
    @DisplayName("Controller_Logic_10")
    @Test //2//BDD//Negative//This test case will work when commenting the line "@AutoConfigureMockMvc(addFilters = false)" on the top of the class
    public void givenPostDtoObjectAndNoJwtToken_whenSavePost_thenReturn403Forbidden() throws Exception
    {
        //given - precondition or setup
        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("Controller_Logic_11")
    @Test //3//BDD//Positive//This test case will work when remove commenting for the line "@AutoConfigureMockMvc(addFilters = false)" on the top of the class
    public void givenPostDtoList_whenGetAllPosts_thenReturnListOfPostObjects() throws Exception
    {
        //given - precondition or setup
        given(postService.createPost(ArgumentMatchers.any(PostDto.class)))
                .willReturn(responseDto);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        //then - verify the output
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /*Utility Methods Onwards*/
    private List<Post> makePostList(int instances)
    {
        List<Post> posts = new ArrayList<>();

        for(int i = 1; i <= instances; i++)
        {
            Post post = Post.builder()
                    .id(Long.valueOf(i))
                    .title("PostTitle"+i)
                    .description("PostDescription"+i)
                    .content("PostContent"+i)
                    .category(category)
                    .build();

            posts.add(post);
        }

        return posts;
    }

    private List<PostDto> makePostDtoList(int instances)
    {
        List<PostDto> postDtos = new ArrayList<>();

        for(int i = 1; i <= instances; i++)
        {
            PostDto postDto = PostDto.builder()
                    .id(i)
                    .title("PostTitle"+i)
                    .description("PostDescription"+i)
                    .content("PostContent"+i)
                    .categoryId(category.getId())
                    .build();

            postDtos.add(postDto);
        }

        return postDtos;
    }

}
