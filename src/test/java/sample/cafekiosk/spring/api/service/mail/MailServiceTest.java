package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    /**
     * @Spy
     * 서비스에서 여러가지 동작을 할때 특정 부분만 mocking을 하고 나머지는 실제 객체 데이터를 토대로
     * 테스트 하고 싶을 때 사용.
     * 사용시 실제 객체를 토대로 작동하기 때문에 when()절 대신 do() 사용
     *
     * ex) doReturn(true).when(mailSendClient).sendEmail(anyString(), anyString(), anyString(),anyString());
     */

    @Mock
    private MailSendClient mailSendClient;
    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks
    private MailService mailService;

    @DisplayName("메일 전송 테스트")
    @Test
    void sendMailTest() {
        // given
        /**
         * @Mock으로 대체할 수 있다,
         * 단 @Mock 사용시 @ExtendWith(MockitoExtension.class) 추가 ->
         * Mockito 사용해서 Mock 사용하는지 알리기 위함
         *
         * MailSendClient mailSendClient = mock(MailSendClient.class);
         * MailSendHistoryRepository mailSendHistoryRepository = mock(MailSendHistoryRepository.class);
         */

        /**
         * @InjectMocks으로 대체 가능
         * MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository);
         */


//        when(mailSendClient.sendMail(anyString(), anyString(), anyString(),anyString()))
//                .thenReturn(true);

        // given절에 when mockito가 어색해서 나온게 BDDMockito. when mockito과 기능 동일함
        given(mailSendClient.sendMail(anyString(), anyString(), anyString(),anyString()))
                .willReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        //then
        assertThat(result).isTrue();
        // mailSendHistoryRepository에 MailSendHistory.class가 1번 저장되는게 맞는지 검증
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));

    }

}