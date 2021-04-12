package com.example.demo.entity;
import com.example.demo.enumeration.Status;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class PostPrototype extends AuditableEntity {

    @Setter
    @Column(nullable = false)
    protected String title;

    @Setter
    @Column(nullable = false)
    protected String text;

    @Setter
    @Column(nullable = false)
    protected Status status;

    public PostPrototype(String title, String text, Status status) {
        this.title = title;
        this.text = text;
        this.status = status;
    }
}
