package ru.any.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "secret_code")
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SecretCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_secret")
    @SequenceGenerator(name = "seq_secret", sequenceName = "seq_secret_id", allocationSize = 1)
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "secret")
    private String secret;

    @Column(name = "attempts_number")
    private Long attemptsNumber;

    @Column(name = "delay_until")
    private LocalDateTime delayUntil;

    @Column(name = "expire_date_time")
    private LocalDateTime expireDateTime;
}
