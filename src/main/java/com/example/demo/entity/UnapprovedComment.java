package com.example.demo.entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@NoArgsConstructor
public class UnapprovedComment extends CommentPrototype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Comment comment;

    public UnapprovedComment(String message, int rating, Comment comment) {
        super(message, rating);
        this.comment = comment;
    }
}
