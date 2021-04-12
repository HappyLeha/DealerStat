package com.example.demo.entity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CommentPrototype extends AuditableEntity{

    @Setter
    private String message;

    @Setter
    @Column(nullable = false)
    private int rating;

    public CommentPrototype(String message, int rating) {
        this.message = message;
        this.rating = rating;
    }
}
