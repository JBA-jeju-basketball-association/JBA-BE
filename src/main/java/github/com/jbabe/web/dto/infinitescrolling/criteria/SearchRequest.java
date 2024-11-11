package github.com.jbabe.web.dto.infinitescrolling.criteria;

import github.com.jbabe.service.exception.BadRequestException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchRequest {
    private final int size;
    private String cursor;
    private Long idCursor;
    private final boolean reverse;
    private final SearchCriteria searchCriteria;
    private CursorHolder cursorHolder;

    public static SearchRequest of(int size, boolean reverse, String searchCriteria) {
        return new SearchRequest(size, reverse, SearchCriteria.setValue(searchCriteria));
    }
    public static SearchRequest fromSize(int size){
        return new SearchRequest(size, false, SearchCriteria.LATEST);
    }
    public SearchRequest withIdAndCursor(Long idCursor, String cursor) {
        this.idCursor = idCursor;
        this.cursor = cursor;
        return this;
    }

    public void makeCursorHolder(){
        try {
            CursorHolder creatingACursor = CursorHolder.fromId(this.idCursor);
            switch (this.searchCriteria) {
                case ACTIVITIES ->
                    this.cursorHolder = creatingACursor.withActivities(Long.parseLong(this.cursor));
                case POPULAR ->
                    this.cursorHolder = creatingACursor.withPopular(Long.parseLong(this.cursor));
                case NAME ->
                    this.cursorHolder = creatingACursor.withName(cursor);
                case LATEST ->
                    this.cursorHolder = creatingACursor.withCreatedAt(LocalDateTime.parse(this.cursor));
                case USER ->
                    this.cursorHolder = creatingACursor.withMember(Long.parseLong(cursor));

            }
        } catch (NullPointerException e) {
            throw new BadRequestException("cursorId is required when cursor exists", null);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("날짜순으로 요청을 했지만 cursor가 날짜 형식이 아닙니다.", this.cursor);
        } catch (NumberFormatException e){
            throw new BadRequestException("수와 관련된 정렬을 요청 했지만 cursor가 숫자 형식이 아닙니다.", this.cursor);
        }
    }
}
