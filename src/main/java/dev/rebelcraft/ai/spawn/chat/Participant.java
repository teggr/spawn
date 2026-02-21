package dev.rebelcraft.ai.spawn.chat;

import jakarta.persistence.*;

@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String avatarUrl;
    private String role;

    public Participant() { }

    public Participant(String id, String name, String avatarUrl, String role) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.role = role;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public Long getLongId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
