package edu.eci.tdse.securetwitter.post.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_id", nullable = false, unique = true)
    private String auth0Id;

    @Column(nullable = false)
    private String name;

    @Column(name = "picture_url")
    private String picture;

    public String getAuth0Id() { return auth0Id; }
    public String getName() { return name; }
    public String getPicture() { return picture; }
}
