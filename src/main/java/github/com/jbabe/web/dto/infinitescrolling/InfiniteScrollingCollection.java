package github.com.jbabe.web.dto.infinitescrolling;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InfiniteScrollingCollection<T extends ScrollingResponseInterface<U>, U> {
    private final List<T> itemsWithNextItem;
    private final int countPerScroll;
    private final U criteria;

    public static <T extends ScrollingResponseInterface<U>, U> InfiniteScrollingCollection<T, U> of(List<T> itemsWithNextCursor, int size, U criteria) {
        return new InfiniteScrollingCollection<>(itemsWithNextCursor, size, criteria);
    }

    public boolean isHasNext() {
        return this.itemsWithNextItem.size() > countPerScroll;
    }

    public List<T> getCurrentScrollItems() {
        return isHasNext() ?
                this.itemsWithNextItem.subList(0, countPerScroll)
                : this.itemsWithNextItem;
    }

    public String getNextCursor() {
        return isHasNext() ?
                this.itemsWithNextItem.get(countPerScroll).nextCursor(criteria)
                : null;
    }

    public Long getNextCursorId() {
        return isHasNext() ?
                this.itemsWithNextItem.get(countPerScroll).nextIdCursor()
                : null;
    }
}