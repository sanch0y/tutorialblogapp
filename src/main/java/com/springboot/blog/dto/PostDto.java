package com.springboot.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Schema(
        description = "PostDto Model Information"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private long id;
    @Schema(
            description = "Blog Post Title"
    )
    @NotEmpty
    @Size(min = 4, message = "Post title must have at least 4 characters")
    private String title;
    @Schema(
            description = "Blog Post Description"
    )
    @NotEmpty
    @Size(min = 10, message = "Post description must have at least 10 characters")
    private String description;
    @Schema(
            description = "Blog Post Content"
    )
    @NotEmpty
    private String content;
    private Set<CommentDto> comments;

    @Schema(
            description = "Blog Post Category"
    )
    private long categoryId;
}
