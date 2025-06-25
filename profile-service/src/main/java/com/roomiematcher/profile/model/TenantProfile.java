package com.roomiematcher.profile.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tenant_profiles")
public class TenantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "budget")
    private Double budget;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "cleanliness_level")
    private Integer cleanlinessLevel;
    
    @Column(name = "noise_tolerance")
    private Integer noiseTolerance;
    
    @Column(name = "smoking")
    private Boolean smoking;
    
    @Column(name = "pets")
    private Boolean pets;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "daily_schedule")
    @Enumerated(EnumType.STRING)
    private DailySchedule dailySchedule;
    
    @Column(name = "guest_frequency")
    @Enumerated(EnumType.STRING)
    private GuestFrequency guestFrequency;
    
    @Column(name = "cooking_habits")
    @Enumerated(EnumType.STRING)
    private CookingHabits cookingHabits;
    
    @Column(name = "music_noise")
    @Enumerated(EnumType.STRING)
    private MusicNoise musicNoise;
    
    @Column(name = "cleaning_frequency")
    @Enumerated(EnumType.STRING)
    private CleaningFrequency cleaningFrequency;
    
    @Column(name = "social_style")
    @Enumerated(EnumType.STRING)
    private SocialStyle socialStyle;
    
    @Column(name = "temperature_preference")
    @Enumerated(EnumType.STRING)
    private TemperaturePreference temperaturePreference;
    
    @Column(name = "lighting_preference")
    @Enumerated(EnumType.STRING)
    private LightingPreference lightingPreference;
    
    @Column(name = "pet_compatibility")
    @Enumerated(EnumType.STRING)
    private PetCompatibility petCompatibility;
    
    @Column(name = "smoking_preference")
    @Enumerated(EnumType.STRING)
    private SmokingPreference smokingPreference;
    
    @Column(name = "parking_needs")
    @Enumerated(EnumType.STRING)
    private ParkingNeeds parkingNeeds;
    
    @Column(name = "overnight_guests")
    @Enumerated(EnumType.STRING)
    private OvernightGuests overnightGuests;
    
    @Column(name = "work_from_home")
    @Enumerated(EnumType.STRING)
    private WorkFromHome workFromHome;
    
    @Column(name = "allergies")
    @Enumerated(EnumType.STRING)
    private Allergies allergies;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "tenantProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PreferredGender> preferredGenders = new HashSet<>();
    
    public TenantProfile() {
    }
    
    public TenantProfile(Long id, Long userId, Double budget, String location, Integer cleanlinessLevel, 
                         Integer noiseTolerance, Boolean smoking, Boolean pets, String gender,
                         DailySchedule dailySchedule, GuestFrequency guestFrequency, 
                         CookingHabits cookingHabits, MusicNoise musicNoise,
                         CleaningFrequency cleaningFrequency, SocialStyle socialStyle,
                         TemperaturePreference temperaturePreference, LightingPreference lightingPreference,
                         PetCompatibility petCompatibility, SmokingPreference smokingPreference,
                         ParkingNeeds parkingNeeds, OvernightGuests overnightGuests,
                         WorkFromHome workFromHome, Allergies allergies,
                         LocalDateTime createdAt, LocalDateTime updatedAt, Set<PreferredGender> preferredGenders) {
        this.id = id;
        this.userId = userId;
        this.budget = budget;
        this.location = location;
        this.cleanlinessLevel = cleanlinessLevel;
        this.noiseTolerance = noiseTolerance;
        this.smoking = smoking;
        this.pets = pets;
        this.gender = gender;
        this.dailySchedule = dailySchedule;
        this.guestFrequency = guestFrequency;
        this.cookingHabits = cookingHabits;
        this.musicNoise = musicNoise;
        this.cleaningFrequency = cleaningFrequency;
        this.socialStyle = socialStyle;
        this.temperaturePreference = temperaturePreference;
        this.lightingPreference = lightingPreference;
        this.petCompatibility = petCompatibility;
        this.smokingPreference = smokingPreference;
        this.parkingNeeds = parkingNeeds;
        this.overnightGuests = overnightGuests;
        this.workFromHome = workFromHome;
        this.allergies = allergies;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.preferredGenders = preferredGenders != null ? preferredGenders : new HashSet<>();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Utility methods for managing preferred genders
    public void addPreferredGender(String gender) {
        PreferredGender preferredGender = new PreferredGender();
        preferredGender.setGender(gender);
        preferredGender.setTenantProfile(this);
        this.preferredGenders.add(preferredGender);
    }
    
    public void removePreferredGender(String gender) {
        this.preferredGenders.removeIf(pg -> pg.getGender().equals(gender));
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Double getBudget() {
        return budget;
    }
    
    public void setBudget(Double budget) {
        this.budget = budget;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getCleanlinessLevel() {
        return cleanlinessLevel;
    }
    
    public void setCleanlinessLevel(Integer cleanlinessLevel) {
        this.cleanlinessLevel = cleanlinessLevel;
    }
    
    public Integer getNoiseTolerance() {
        return noiseTolerance;
    }
    
    public void setNoiseTolerance(Integer noiseTolerance) {
        this.noiseTolerance = noiseTolerance;
    }
    
    public Boolean getSmoking() {
        return smoking;
    }
    
    public void setSmoking(Boolean smoking) {
        this.smoking = smoking;
    }
    
    public Boolean getPets() {
        return pets;
    }
    
    public void setPets(Boolean pets) {
        this.pets = pets;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public DailySchedule getDailySchedule() {
        return dailySchedule;
    }
    
    public void setDailySchedule(DailySchedule dailySchedule) {
        this.dailySchedule = dailySchedule;
    }
    
    public GuestFrequency getGuestFrequency() {
        return guestFrequency;
    }
    
    public void setGuestFrequency(GuestFrequency guestFrequency) {
        this.guestFrequency = guestFrequency;
    }
    
    public CookingHabits getCookingHabits() {
        return cookingHabits;
    }
    
    public void setCookingHabits(CookingHabits cookingHabits) {
        this.cookingHabits = cookingHabits;
    }
    
    public MusicNoise getMusicNoise() {
        return musicNoise;
    }
    
    public void setMusicNoise(MusicNoise musicNoise) {
        this.musicNoise = musicNoise;
    }
    
    public CleaningFrequency getCleaningFrequency() {
        return cleaningFrequency;
    }
    
    public void setCleaningFrequency(CleaningFrequency cleaningFrequency) {
        this.cleaningFrequency = cleaningFrequency;
    }
    
    public SocialStyle getSocialStyle() {
        return socialStyle;
    }
    
    public void setSocialStyle(SocialStyle socialStyle) {
        this.socialStyle = socialStyle;
    }
    
    public TemperaturePreference getTemperaturePreference() {
        return temperaturePreference;
    }
    
    public void setTemperaturePreference(TemperaturePreference temperaturePreference) {
        this.temperaturePreference = temperaturePreference;
    }
    
    public LightingPreference getLightingPreference() {
        return lightingPreference;
    }
    
    public void setLightingPreference(LightingPreference lightingPreference) {
        this.lightingPreference = lightingPreference;
    }
    
    public PetCompatibility getPetCompatibility() {
        return petCompatibility;
    }
    
    public void setPetCompatibility(PetCompatibility petCompatibility) {
        this.petCompatibility = petCompatibility;
    }
    
    public SmokingPreference getSmokingPreference() {
        return smokingPreference;
    }
    
    public void setSmokingPreference(SmokingPreference smokingPreference) {
        this.smokingPreference = smokingPreference;
    }
    
    public ParkingNeeds getParkingNeeds() {
        return parkingNeeds;
    }
    
    public void setParkingNeeds(ParkingNeeds parkingNeeds) {
        this.parkingNeeds = parkingNeeds;
    }
    
    public OvernightGuests getOvernightGuests() {
        return overnightGuests;
    }
    
    public void setOvernightGuests(OvernightGuests overnightGuests) {
        this.overnightGuests = overnightGuests;
    }
    
    public WorkFromHome getWorkFromHome() {
        return workFromHome;
    }
    
    public void setWorkFromHome(WorkFromHome workFromHome) {
        this.workFromHome = workFromHome;
    }
    
    public Allergies getAllergies() {
        return allergies;
    }
    
    public void setAllergies(Allergies allergies) {
        this.allergies = allergies;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Set<PreferredGender> getPreferredGenders() {
        return preferredGenders;
    }
    
    public void setPreferredGenders(Set<PreferredGender> preferredGenders) {
        this.preferredGenders = preferredGenders;
    }
    
    // Builder pattern implementation
    public static TenantProfileBuilder builder() {
        return new TenantProfileBuilder();
    }
    
    public static class TenantProfileBuilder {
        private Long id;
        private Long userId;
        private Double budget;
        private String location;
        private Integer cleanlinessLevel;
        private Integer noiseTolerance;
        private Boolean smoking;
        private Boolean pets;
        private String gender;
        private DailySchedule dailySchedule;
        private GuestFrequency guestFrequency;
        private CookingHabits cookingHabits;
        private MusicNoise musicNoise;
        private CleaningFrequency cleaningFrequency;
        private SocialStyle socialStyle;
        private TemperaturePreference temperaturePreference;
        private LightingPreference lightingPreference;
        private PetCompatibility petCompatibility;
        private SmokingPreference smokingPreference;
        private ParkingNeeds parkingNeeds;
        private OvernightGuests overnightGuests;
        private WorkFromHome workFromHome;
        private Allergies allergies;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Set<PreferredGender> preferredGenders = new HashSet<>();
        
        public TenantProfileBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public TenantProfileBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public TenantProfileBuilder budget(Double budget) {
            this.budget = budget;
            return this;
        }
        
        public TenantProfileBuilder location(String location) {
            this.location = location;
            return this;
        }
        
        public TenantProfileBuilder cleanlinessLevel(Integer cleanlinessLevel) {
            this.cleanlinessLevel = cleanlinessLevel;
            return this;
        }
        
        public TenantProfileBuilder noiseTolerance(Integer noiseTolerance) {
            this.noiseTolerance = noiseTolerance;
            return this;
        }
        
        public TenantProfileBuilder smoking(Boolean smoking) {
            this.smoking = smoking;
            return this;
        }
        
        public TenantProfileBuilder pets(Boolean pets) {
            this.pets = pets;
            return this;
        }
        
        public TenantProfileBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }
        
        public TenantProfileBuilder dailySchedule(DailySchedule dailySchedule) {
            this.dailySchedule = dailySchedule;
            return this;
        }
        
        public TenantProfileBuilder guestFrequency(GuestFrequency guestFrequency) {
            this.guestFrequency = guestFrequency;
            return this;
        }
        
        public TenantProfileBuilder cookingHabits(CookingHabits cookingHabits) {
            this.cookingHabits = cookingHabits;
            return this;
        }
        
        public TenantProfileBuilder musicNoise(MusicNoise musicNoise) {
            this.musicNoise = musicNoise;
            return this;
        }
        
        public TenantProfileBuilder cleaningFrequency(CleaningFrequency cleaningFrequency) {
            this.cleaningFrequency = cleaningFrequency;
            return this;
        }
        
        public TenantProfileBuilder socialStyle(SocialStyle socialStyle) {
            this.socialStyle = socialStyle;
            return this;
        }
        
        public TenantProfileBuilder temperaturePreference(TemperaturePreference temperaturePreference) {
            this.temperaturePreference = temperaturePreference;
            return this;
        }
        
        public TenantProfileBuilder lightingPreference(LightingPreference lightingPreference) {
            this.lightingPreference = lightingPreference;
            return this;
        }
        
        public TenantProfileBuilder petCompatibility(PetCompatibility petCompatibility) {
            this.petCompatibility = petCompatibility;
            return this;
        }
        
        public TenantProfileBuilder smokingPreference(SmokingPreference smokingPreference) {
            this.smokingPreference = smokingPreference;
            return this;
        }
        
        public TenantProfileBuilder parkingNeeds(ParkingNeeds parkingNeeds) {
            this.parkingNeeds = parkingNeeds;
            return this;
        }
        
        public TenantProfileBuilder overnightGuests(OvernightGuests overnightGuests) {
            this.overnightGuests = overnightGuests;
            return this;
        }
        
        public TenantProfileBuilder workFromHome(WorkFromHome workFromHome) {
            this.workFromHome = workFromHome;
            return this;
        }
        
        public TenantProfileBuilder allergies(Allergies allergies) {
            this.allergies = allergies;
            return this;
        }
        
        public TenantProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public TenantProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public TenantProfileBuilder preferredGenders(Set<PreferredGender> preferredGenders) {
            this.preferredGenders = preferredGenders;
            return this;
        }
        
        public TenantProfile build() {
            return new TenantProfile(id, userId, budget, location, cleanlinessLevel, noiseTolerance, 
                                    smoking, pets, gender, dailySchedule, guestFrequency, cookingHabits, 
                                    musicNoise, cleaningFrequency, socialStyle, temperaturePreference, 
                                    lightingPreference, petCompatibility, smokingPreference, parkingNeeds, 
                                    overnightGuests, workFromHome, allergies, createdAt, updatedAt, preferredGenders);
        }
    }
} 