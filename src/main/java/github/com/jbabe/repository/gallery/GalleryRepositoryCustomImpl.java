package github.com.jbabe.repository.gallery;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import github.com.jbabe.repository.galleryImg.GalleryImg;
import github.com.jbabe.repository.galleryImg.QGalleryImg;
import github.com.jbabe.web.dto.SearchCriteriaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class GalleryRepositoryCustomImpl implements GalleryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QGallery qGallery = QGallery.gallery;

    private Page<Gallery> checkElementCountAndReturnPage(QGallery qGallery, BooleanExpression predicate, List<Gallery> galleryList, Pageable pageable) {
        Long total = jpaQueryFactory.select(qGallery.galleryId.count())
                .from(qGallery)
                .where(predicate)
                .fetchOne();
        return new PageImpl<>(galleryList, pageable, total != null ? total : 0);

    }

    @Override
    public Page<Gallery> getGalleryList(Pageable pageable, boolean official) {


        BooleanExpression predicate = qGallery.isOfficial.eq(official)
                .and(qGallery.galleryStatus.eq(Gallery.GalleryStatus.NORMAL));

        List<Gallery> galleryList = getGalleryListWithThumbnailQuery(qGallery, predicate, pageable);

        return checkElementCountAndReturnPage(qGallery, predicate, galleryList, pageable);
    }

    @Override
    public Page<Gallery> getGalleryManageList(Pageable pageable, Boolean official, String keyword, SearchCriteriaEnum searchCriteria) {

        BooleanExpression predicate = null;
        if (official != null) predicate = qGallery.isOfficial.eq(official);
        if (keyword != null) {
            predicate = switch (searchCriteria) {
                case EMAIL ->
                        predicate != null ? predicate.and(qGallery.user.email.containsIgnoreCase(keyword)) : qGallery.user.email.containsIgnoreCase(keyword);
                case TITLE ->
                        predicate != null ? predicate.and(qGallery.name.containsIgnoreCase(keyword)) : qGallery.name.containsIgnoreCase(keyword);
                case ID ->
                        predicate != null ? predicate.and(qGallery.galleryId.eq(Integer.valueOf(keyword))) : qGallery.galleryId.eq(Integer.valueOf(keyword));
                default -> predicate;
            };
        }
        List<Gallery> galleries = getGalleryListWithUserEmailAndAllImages(qGallery, predicate, pageable);

        return checkElementCountAndReturnPage(qGallery, predicate, galleries, pageable);
    }

    @Override
    public Page<Gallery> searchGalleryList(boolean official, String keyword, Pageable pageable) {
        QGallery qGallery = QGallery.gallery;

        BooleanExpression predicate = qGallery.isOfficial.eq(official)
                .and(qGallery.name.containsIgnoreCase(keyword))
                .and(qGallery.galleryStatus.eq(Gallery.GalleryStatus.NORMAL));

        List<Gallery> galleryList = getGalleryListWithThumbnailQuery(qGallery, predicate, pageable);

        // 갤러리 이미지를 가져오는 방법 1 n+1 문제로 탈락
//        galleryList.forEach(gallery -> {
//            GalleryImg galleryImg = jpaQueryFactory.selectFrom(qGalleryImg)
//                    .where(qGalleryImg.gallery.eq(gallery))
//                    .fetchFirst();
//            gallery.setGalleryImgs(List.of(galleryImg));
//        });

        return checkElementCountAndReturnPage(qGallery, predicate, galleryList, pageable);
    }

    private List<Gallery> getGalleryListWithUserEmailAndAllImages(QGallery qGallery, BooleanExpression predicate, Pageable pageable) {
        List<Tuple> results = jpaQueryFactory.select(qGallery, qGallery.user.email)
                .from(qGallery)
                .where(predicate)
                .orderBy(qGallery.createAt.desc())
                .groupBy(qGallery.galleryId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Integer> galleryIds = results.stream().map(tuple -> Objects.requireNonNull(tuple.get(qGallery)).getGalleryId()).toList();
        QGalleryImg qGalleryImg = QGalleryImg.galleryImg;
        List<GalleryImg> galleryImgs = jpaQueryFactory.select(qGalleryImg)
                .from(qGalleryImg)
                .where(qGalleryImg.gallery.galleryId.in(galleryIds))
                .fetch();


        return results.stream().map(tuple -> {
            Gallery gallery = tuple.get(qGallery);
            Objects.requireNonNull(gallery)
                    .setUserEmail(tuple.get(qGallery.user.email));
            gallery.setGalleryImgs(galleryImgs.stream()
                    .filter(galleryImg -> galleryImg.getGallery().getGalleryId().equals(gallery.getGalleryId()))
                    .toList());

            return gallery;
        }).toList();
    }

    private List<Gallery> getGalleryListWithThumbnailQuery(QGallery qGallery, BooleanExpression predicate, Pageable pageable) {
        QGalleryImg qGalleryImg = QGalleryImg.galleryImg;
//        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable.getSort(), qGallery);


        List<Tuple> results = jpaQueryFactory.select(qGallery, qGalleryImg.fileName, qGalleryImg.fileUrl)
                .from(qGallery)
                .leftJoin(qGallery.galleryImgs, qGalleryImg)
                .where(predicate)
                .orderBy(qGallery.createAt.desc())
                .groupBy(qGallery.galleryId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return results.stream().map(tuple -> {
            Gallery gallery = tuple.get(qGallery);
            GalleryImg galleryImg = tuple.get(qGalleryImg.fileUrl) != null ?
                    GalleryImg.builder()
                            .fileUrl(tuple.get(qGalleryImg.fileUrl))
                            .fileName(tuple.get(qGalleryImg.fileName))
                            .build()
                    : null;
            if (galleryImg != null) {
                Objects.requireNonNull(gallery)
                        .setGalleryImgs(Collections.singletonList(galleryImg));
            } else {
                Objects.requireNonNull(gallery)
                        .setGalleryImgs(Collections.emptyList());
            }
            return gallery;
        }).toList();
    }

//    private List<OrderSpecifier<?>> getOrderSpecifiers(Sort sort, QGallery qGallery) {
//        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
//        for (Sort.Order order : sort) {
//            PathBuilder<Object> pathBuilder = new PathBuilder<>(Object.class, qGallery.getMetadata());
//            OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC,
//                    pathBuilder.get(order.getProperty()));
//            orderSpecifiers.add(orderSpecifier);
//        }
//        return orderSpecifiers;
//    }
}
