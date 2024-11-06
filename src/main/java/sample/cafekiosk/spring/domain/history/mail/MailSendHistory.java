package sample.cafekiosk.spring.domain.history.mail;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MailSendHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromMail;
    private String toEMail;
    private String subject;
    private String content;

    @Builder
    public MailSendHistory(String fromMail, String toEMail, String subject, String content) {
        this.fromMail = fromMail;
        this.toEMail = toEMail;
        this.subject = subject;
        this.content = content;
    }
}
