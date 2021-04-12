package com.example.demo.entity;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
@Entity
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @NaturalId
    private String name;

    @ManyToMany
    @ToString.Exclude
    private List<Post> posts;

    @ManyToMany
    @ToString.Exclude
    private List<UnapprovedPost> unapprovedPosts;

    public Game(String name) {
        this.name = name;
    }
}
