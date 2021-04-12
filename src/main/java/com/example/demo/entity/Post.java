package com.example.demo.entity;
import com.example.demo.enumeration.Status;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@NoArgsConstructor
public class Post extends PostPrototype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Column(nullable = false)
    private boolean approved;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @Setter
    @OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private UnapprovedPost unapprovedPost;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Comment> comments;

    @Setter
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "posts_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> games;

    @Builder
    public Post(String title, String text, Status status, boolean approved,
                User user, List<Game> games) {
        super(title, text, status);
        this.approved = approved;
        this.author = user;
        this.games = games;
    }
}
