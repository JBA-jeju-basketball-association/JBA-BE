package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.web.dto.post.PostsListDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);


    @Mapping(target = "title", source = "name")
    @Mapping(target = "writer", source = "post.user.name")
    @Mapping(target = "createAt", dateFormat = "yyyy-MM-dd")
    PostsListDto PostToPostsListDto(Post post);
}
