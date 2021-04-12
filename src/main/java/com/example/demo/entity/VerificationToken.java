package com.example.demo.entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.*;
import java.util.Calendar;
import java.util.UUID;

@EqualsAndHashCode
@ToString
@Getter
@Entity
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expiryDate;

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    public VerificationToken(User user) {
        Calendar calendar = Calendar.getInstance();

        this.user = user;
        this.token = UUID.randomUUID().toString();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        this.expiryDate = calendar;
    }
}
