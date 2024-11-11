package github.com.jbabe.web.dto.infinitescrolling;

public interface ScrollingResponseInterface<U> {
    String nextCursor(U criteria);
    long nextIdCursor();

}