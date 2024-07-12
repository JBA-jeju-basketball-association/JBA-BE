package github.com.jbabe.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

public class  QueryDslUtil<T> {

    public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);
        //  메서드 내부에서는 Expressions.path() 메서드를 사용하여 Path<Object> 객체를 생성합니다. 이 객체는 부모 경로와 필드 이름을 조합하여 특정 필드를 가리키는 경로를 나타냅니다.
        return new OrderSpecifier(order, fieldPath);
    }

    public static OrderSpecifier[] getOrderSpecifiers(@NotNull Pageable pageable, @NotNull Class klass) {

        // orderVariable must match the variable of FROM
        String className = klass.getSimpleName();
        final String orderVariable = String.valueOf(Character.toLowerCase(className.charAt(0))).concat(className.substring(1));

        return pageable.getSort().stream()
                .map(order -> new OrderSpecifier(
                        Order.valueOf(order.getDirection().toString()),
                        new PathBuilder(klass, orderVariable).get(order.getProperty()))
                )
                .toArray(OrderSpecifier[]::new);
    }
}


