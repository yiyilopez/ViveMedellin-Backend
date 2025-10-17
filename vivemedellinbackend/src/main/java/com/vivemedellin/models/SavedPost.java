package com.vivemedellin.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "saved_posts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class SavedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "saved_date", nullable = false)
    private Date savedDate = new Date();
}
