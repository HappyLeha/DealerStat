package com.example.demo.entity;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Entity
@NoArgsConstructor
public class Comment extends CommentPrototype{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Column(nullable = false)
    private boolean approved;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @OneToOne(mappedBy = "comment", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private UnapprovedComment unapprovedComment;

    public Comment(String message, int rating, boolean approved,
                   Post post, User user) {
        super(message, rating);
        this.approved = approved;
        this.post = post;
        this.author = user;
    }
}
