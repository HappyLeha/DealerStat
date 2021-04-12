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
public class UnapprovedPost extends PostPrototype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Post post;

    @Setter
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "unapproved_posts_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> games;

    public UnapprovedPost(String title, String text, Status status,
                          List<Game> games, Post post) {
        super(title, text, status);
        this.post = post;
        this.games = games;
    }
}
