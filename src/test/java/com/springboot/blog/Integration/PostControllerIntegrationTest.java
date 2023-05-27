package com.springboot.blog.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.dto.LoginDto;
import com.springboot.blog.dto.PostDto;
import com.springboot.blog.dto.RegisterDto;
import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.service.AuthService;
import com.springboot.blog.service.impl.AuthServiceImpl;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    private Post postEntity;

    private PostDto requestDto;

    private PostDto responseDto;

    private RegisterDto registerAdminDto;

    private RegisterDto registerDto;

    private LoginDto loginDto;

    private Category category;

    @BeforeEach
    void setup()
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

        registerDto = RegisterDto.builder()
                .name("Tamanna Tabassum")
                .username("orthi")
                .email("orthi@gmail.com")
                .password("orthi123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("orthi@gmail.com")
                .password("orthi123")
                .build();

        userRepository.deleteAll();
        categoryRepository.deleteAll();
        postRepository.deleteAll();
    }


    @DisplayName("Integration_Test_01")
    @Test
    //1//BDD//Positive//
    public void givenPostDtoObjectWithCategoryAndJwtToken_whenSavePost_thenReturnPostObject() throws Exception
    {
        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        //Save category for not to get 404 error
        categoryRepository.save(category);

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

    @DisplayName("Integration_Test_02")
    @Test
    //2//BDD//Negative//
    public void givenPostDtoObjectWithCategoryAndWrongJwtToken_whenSavePost_thenReturnPostObject() throws Exception
    {
        String jwtToken = "Bearer " + "Wrong Token";

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("An Authentication object was not found in the SecurityContext")));
    }

    @DisplayName("Integration_Test_03")
    @Test
    //3//BDD//Negative//
    public void givenPostDtoObjectWithJwtTokenAndWrongCategoryId_whenSavePost_thenReturnNotFoundError() throws Exception
    {
        /*Authentication Preprocedure Start*/
        registerAdminDto = RegisterDto.builder()
                .name("Sanchoy")
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        loginDto = LoginDto.builder()
                .usernameOrEmail("admin@gmail.com")
                .password("admin123")
                .build();

        authServiceImpl.registerAdmin(registerAdminDto);

        String jwtToken = "Bearer " + authService.login(loginDto);
        /*Authentication Preprocedure End*/

        /*Don't save category*/

        // when - action or behaviour that we are going to test
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then - verify the output
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Category not found with id : '"+requestDto.getCategoryId()+"'")));
    }


}
