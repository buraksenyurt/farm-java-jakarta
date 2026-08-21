package com.lectures.memo.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Memo title cannot be empty")
    @Size(min = 20, message = "Title should not be less than 20 chars")
    @Size(max = 100, message = "Title should not be more than 100 chars")
    @Column(length = 100)
    private String title;

    @NotEmpty(message = "Memo summary cannot be empty")
    @Size(min = 50, message = "Summary should not be less than 50 chars")
    @Size(max = 500, message = "Summary should not be more than 500 chars")
    @Column(length = 500)
    private String summary;

    @Pattern(
            regexp = "^$|^(https?://)?[\\w.-]+\\.[a-zA-Z]{2,}(/\\S*)?$",
            message = "Link geçerli bir URL formatında olmalıdır"
    )
    private String link;

    @Enumerated(EnumType.STRING)
    private MemoCategory category;

    private LocalDate dateCreated;

    @PrePersist
    @PreUpdate
    private void init() {
        setDateCreated(LocalDate.now());
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public MemoCategory getCategory() {
        return category;
    }

    public void setCategory(MemoCategory category) {
        this.category = category;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }
}
