package com.jeeva.calorietrackerbackend.model;


import jakarta.persistence.*;


import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    private String imageUrl;
    private String notes;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Override
    public String toString() {
        return "Food{" +
                "uuid=" + uuid +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", mealType=" + mealType +
                ", imageUrl='" + imageUrl + '\'' +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UUID getUuid() {
        return uuid;
    }

    public MealType getMealType() {
        return mealType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNotes() {
        return notes;
    }
}
