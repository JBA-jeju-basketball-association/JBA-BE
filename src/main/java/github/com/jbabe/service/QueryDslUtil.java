//package github.com.jbabe.service;
//
//import com.querydsl.core.types.Order;
//import com.querydsl.core.types.OrderSpecifier;
//import com.querydsl.core.types.Path;
//import com.querydsl.core.types.dsl.DateTimePath;
//import com.querydsl.core.types.dsl.Expressions;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.springframework.util.ObjectUtils.isEmpty;
//
//public class QueryDslUtil {
//    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
//        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);
//
//        return new OrderSpecifier<>(order, fieldPath);
//    }
//
//    public static List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable, String entityType) {
//
//        List<OrderSpecifier<?>> orders = new ArrayList<>();
//
//        if (!isEmpty(pageable.getSort())) {
//            for (Sort.Order order : pageable.getSort()) {
//                if ("createdAt".equals(order.getProperty())) {
//                    Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
//                    DateTimePath<LocalDateTime> path;
//                    switch (entityType) {
//                        case "recruitment":
//                            path = QRecruitment.recruitment.createdAt;
//                            break;
//                        case "project":
//                            path = QProject.project.createdAt;
//                            break;
//                        default:
//                            throw new IllegalArgumentException("Entity의 타입이 아닙니다.");
//                    }
//                    OrderSpecifier<?> orderSpecifier = QueryDslUtil.getSortedColumn(direction, path, "createdAt");
//                    orders.add(orderSpecifier);
//                }
//            }
//        }
//
//        return orders;
//    }
//}
