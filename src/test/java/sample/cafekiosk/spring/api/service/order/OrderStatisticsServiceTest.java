package sample.cafekiosk.spring.api.service.order;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.orderProduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static sample.cafekiosk.spring.domain.order.OrderStatus.PAYMENT_COMPLETED;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;


class OrderStatisticsServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @DisplayName("결제 완료 주문들을 조회하여 매출 통계 메일을 전송한다.")
    @Test
    void sendMailTest() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 11, 6, 0, 0);

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);

        createPaymentCompletedOrder(LocalDateTime.of(2024,11,5,23,59, 59), products);
        createPaymentCompletedOrder(now, products);
        createPaymentCompletedOrder(LocalDateTime.of(2024,11,6,23,59, 59), products);
        createPaymentCompletedOrder(LocalDateTime.of(2024, 11, 7, 0, 0), products);

        // stubbing -> mock 객체에 원하는 행위를 저장
        // mailSendClient에 sendMail 호출시 파라미터로 String class 어떤값이든 들어오면 true 반환
        // mocking 작업이 있는곳에서는 Transactional 걸지 않는게 좋음(메일 작업 같은 네트워크가 긴 작업에서는 굳이?)
       Mockito.when(mailSendClient.sendMail(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);


        // when
        boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2024, 11, 6), "test@test.com");

        //then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 9000원 입니다.");
    }

    private Order createPaymentCompletedOrder(LocalDateTime now, List<Product> products) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(PAYMENT_COMPLETED)
                .registeredDateTime(now)
                .build();

        return orderRepository.save(order);
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

}