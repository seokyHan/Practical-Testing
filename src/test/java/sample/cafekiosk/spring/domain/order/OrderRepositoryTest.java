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
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static sample.cafekiosk.spring.domain.order.OrderStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@Transactional
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("하루치 결제 완료된 주문목록을 조회한다.")
    @Test
    void findOrdersByTest() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = LocalDate.now().atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().plusDays(1).atStartOfDay();

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);

        Order order1 = create(products, registeredDateTime, PAYMENT_COMPLETED);
        Order order2 = create(products, endDateTime, PAYMENT_FAILED);
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

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }

    public Order create(List<Product> products, LocalDateTime registeredDatetime, OrderStatus status) {
        return Order.builder()
                .orderStatus(status)
                .products(products)
                .registeredDateTime(registeredDatetime)
                .build();
    }

}