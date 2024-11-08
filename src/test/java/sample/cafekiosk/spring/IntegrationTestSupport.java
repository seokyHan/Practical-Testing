package sample.cafekiosk.spring;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.client.mail.MailSendClient;

// 통합 테스트 환경 비용 절감을 위한 클래스
@ActiveProfiles("test") // application.yml test profile로 동작
@SpringBootTest
public abstract class IntegrationTestSupport {

    // 해당 클래스를 상속받는 곳에서 MockBean을 사용하고자 할 경우 분리
    // 분리하지 않을 경우 Spring boot 서버가 다시 올라감
    @MockBean
    protected MailSendClient mailSendClient;
}
