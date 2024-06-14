package github.com.jbabe.web.dto.myPage;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import github.com.jbabe.web.dto.gallery.GalleryListDto;
import github.com.jbabe.web.dto.post.PostsListDto;

import java.io.IOException;

public class MyPageSerializer extends JsonSerializer<MyPage<?>> {

    @Override
    public void serialize(MyPage<?> response, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();


        gen.writeNumberField("totalPages", response.getTotalPages());

        if (response.getType() == PostsListDto.class) {
            gen.writeNumberField("totalPosts", response.getTotalElements());
            gen.writeObjectField("posts", response.getContent());
        } else if (response.getType() == GalleryListDto.class) {
            gen.writeNumberField("totalGalleries", response.getTotalElements());
            gen.writeObjectField("galleries", response.getContent());
        } else {
            gen.writeNumberField("totalElements", response.getTotalElements());
            gen.writeObjectField("content", response.getContent());
        }

        gen.writeEndObject();
    }
}
