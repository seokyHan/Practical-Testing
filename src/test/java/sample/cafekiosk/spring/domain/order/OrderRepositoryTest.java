package sample.cafekiosk.spring.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.orderProduct.OrderProduct;
import sample.cafekiosk.spring.domain.product.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static sample.cafekiosk.spring.domain.order.OrderStatus.PAYMENT_COMPLETED;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@Transactional
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("하루치 결제 완료된 주문목록을 조회한다.")
    @Test
    void findOrdersByTest() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = LocalDate.now().atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().plusDays(1).atStartOfDay();

        List<Product> products1 = List.of(createProduct("001", "아메리카노",1000));
        List<Product> products2 = List.of(createProduct("002", "카페라떼",3000));
        Order order1 = Order.create(products1, registeredDateTime);
        Order order2 = Order.create(products2, endDateTime);
        orderRepository.saveAll(List.of(order1,order2));

        // when
        List<Order> orders = orderRepository.findOrdersBy(startDateTime, endDateTime, PAYMENT_COMPLETED);


        //then
        assertThat(orders).hasSize(1)
                .extracting("id", "orderStatus")
                .containsExactlyInAnyOrder(
                        tuple(1L, PAYMENT_COMPLETED)
                ); // 순서 상관없이 맞는지 체크
    }

    private Product createProduct(String productNumber, String name, int price) {
        return Product.builder()
                .type(HANDMADE)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name(name)
                .build();
    }

}