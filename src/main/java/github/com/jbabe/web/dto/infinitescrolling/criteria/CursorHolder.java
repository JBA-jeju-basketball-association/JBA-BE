package github.com.jbabe.web.dto.infinitescrolling.criteria;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CursorHolder {
    private final Long idCursor;
    private String nameCursor;
    private Long memberCursor;
    private Long popularCursor;
    private Long activitiesCursor;
    private LocalDateTime createdAtCursor;

    public static CursorHolder fromId(Long idCursor) {
        if (idCursor == null) throw new NullPointerException("cursorId is null");
        return new CursorHolder(idCursor);
    }


    public CursorHolder withCreatedAt(LocalDateTime cursor) {
        this.createdAtCursor = cursor;
        return this;
    }
    public CursorHolder  withName(String cursor) {
        this.nameCursor = cursor;
        return this;
    }
    public CursorHolder withMember(Long cursor) {
        this.memberCursor = cursor;
        return this;
    }
    public CursorHolder withPopular(Long cursor) {
        this.popularCursor = cursor;
        return this;
    }
    public CursorHolder withActivities(Long cursor) {
        this.activitiesCursor = cursor;
        return this;
    }

}
