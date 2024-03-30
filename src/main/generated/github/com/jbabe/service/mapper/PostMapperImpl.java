package github.com.jbabe.service.mapper;

import github.com.jbabe.repository.post.Post;
import github.com.jbabe.repository.user.User;
import github.com.jbabe.web.dto.post.PostResponseDto;
import github.com.jbabe.web.dto.post.PostsListDto;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-30T18:38:32+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (JetBrains s.r.o.)"
)
public class PostMapperImpl implements PostMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_0159776256 = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );

    @Override
    public PostsListDto PostToPostsListDto(Post post) {
        if ( post == null ) {
            return null;
        }

        PostsListDto postsListDto = new PostsListDto();

        postsListDto.setTitle( post.getName() );
        postsListDto.setWriter( postUserName( post ) );
        if ( post.getCreateAt() != null ) {
            postsListDto.setCreateAt( dateTimeFormatter_yyyy_MM_dd_0159776256.format( post.getCreateAt() ) );
        }
        if ( post.getPostId() != null ) {
            postsListDto.setPostId( post.getPostId() );
        }
        postsListDto.setIsAnnouncement( post.getIsAnnouncement() );
        if ( post.getViewCount() != null ) {
            postsListDto.setViewCount( post.getViewCount() );
        }

        return postsListDto;
    }

    @Override
    public PostResponseDto PostToPostResponseDto(Post post) {
        if ( post == null ) {
            return null;
        }

        PostResponseDto postResponseDto = new PostResponseDto();

        if ( post.getCreateAt() != null ) {
            postResponseDto.setCreateAt( dateTimeFormatter_yyyy_MM_dd_0159776256.format( post.getCreateAt() ) );
        }
        postResponseDto.setWriter( postUserName( post ) );
        postResponseDto.setPostImgs( imgUrl( post.getPostImgs() ) );
        postResponseDto.setPostAttachedFiles( postAttachedFile( post.getPostAttachedFiles() ) );
        postResponseDto.setTitle( post.getName() );
        if ( post.getPostId() != null ) {
            postResponseDto.setPostId( post.getPostId() );
        }
        if ( post.getViewCount() != null ) {
            postResponseDto.setViewCount( post.getViewCount() );
        }
        postResponseDto.setContent( post.getContent() );

        return postResponseDto;
    }

    private String postUserName(Post post) {
        if ( post == null ) {
            return null;
        }
        User user = post.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
