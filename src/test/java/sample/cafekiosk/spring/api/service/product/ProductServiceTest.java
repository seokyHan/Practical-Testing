package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;


class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeAll
    static void beforeAll() {
        // before class
        // 테스트 클래스 전체 실행 전에 작업을 위해

    }

    @BeforeEach
    void setUp() {
        // before method
        // 각 테스트가 시작하기 전에 작업을 위해
        // 값을 주입받는 용도로 사용시 테스트간 공유 자원을 사용해서 테스트간 결합도를 높일 수 있기 때문에 지양하는 것이 좋다.

        // 아래와 같은 상황이면 사용해도 괜찮다.
        // 각 테스트 입장에서 봤을 때 아예 몰라도 테스트 내용을 이해하는 데에 문제가 없는가?
        // 수정해도 모든 테스트에 영향을 주지 않는가?

    }
    @AfterAll
    void afterTest() {
        // after class
        // 테스트 클래스 전체 실행 후에 작업을 위해

    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }


    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최근 상품의 상품번호에서 1 증가한 값이다.")
    @Test
    void createProductTest() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        productRepository.save(product1);

        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        // when
        ProductResponse response = productService.createProduct(request.toServiceRequest());

        //then
        assertThat(response)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("002", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", HANDMADE, SELLING, "아메리카노", 4000),
                        tuple("002", HANDMADE, SELLING, "카푸치노", 5000)
                );


    }

    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품번호는 001 이다.")
    @Test
    void createProductWhenProductsIsEmptyTest() {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        // when
        ProductResponse response = productService.createProduct(request.toServiceRequest());

        //then
        assertThat(response)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("001", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", HANDMADE, SELLING, "카푸치노", 5000)
                );

    }

    // 클래스마다 아래와 같이 builder 메서드를 작성하면 번거롭기 때문에 추상클래스로 분리해서 사용할 수 있지만 추천은 X
  private Product createProduct(String productNumber, ProductType type, ProductSellingStatus status, String name, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(status)
                .name(name)
                .price(price)
                .build();
    }
}