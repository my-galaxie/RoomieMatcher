package com.roomiematcher.common.dto.profile;

import java.util.Set;

public class TenantProfileDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String gender;
    private Double budget;
    private String location;
    private Integer cleanlinessLevel;
    private Integer noiseTolerance;
    private Boolean smoking;
    private Boolean pets;
    private Set<String> preferredGenders;
    
    // New tenant preferences
    private String dailySchedule;
    private String guestFrequency;
    private String cookingHabits;
    private String musicNoise;
    private String cleaningFrequency;
    private String socialStyle;
    private String temperaturePreference;
    private String lightingPreference;
    private String petCompatibility;
    private String smokingPreference;
    private String parkingNeeds;
    private String overnightGuests;
    private String workFromHome;
    private String allergies;
    
    public TenantProfileDTO() {
    }
    
    public TenantProfileDTO(Long id, Long userId, String name, String email, String gender, Double budget, 
                           String location, Integer cleanlinessLevel, Integer noiseTolerance, 
                           Boolean smoking, Boolean pets, Set<String> preferredGenders,
                           String dailySchedule, String guestFrequency, String cookingHabits,
                           String musicNoise, String cleaningFrequency, String socialStyle,
                           String temperaturePreference, String lightingPreference, 
                           String petCompatibility, String smokingPreference,
                           String parkingNeeds, String overnightGuests,
                           String workFromHome, String allergies) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.budget = budget;
        this.location = location;
        this.cleanlinessLevel = cleanlinessLevel;
        this.noiseTolerance = noiseTolerance;
        this.smoking = smoking;
        this.pets = pets;
        this.preferredGenders = preferredGenders;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
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
    
    public Set<String> getPreferredGenders() {
        return preferredGenders;
    }
    
    public void setPreferredGenders(Set<String> preferredGenders) {
        this.preferredGenders = preferredGenders;
    }
    
    public String getDailySchedule() {
        return dailySchedule;
    }
    
    public void setDailySchedule(String dailySchedule) {
        this.dailySchedule = dailySchedule;
    }
    
    public String getGuestFrequency() {
        return guestFrequency;
    }
    
    public void setGuestFrequency(String guestFrequency) {
        this.guestFrequency = guestFrequency;
    }
    
    public String getCookingHabits() {
        return cookingHabits;
    }
    
    public void setCookingHabits(String cookingHabits) {
        this.cookingHabits = cookingHabits;
    }
    
    public String getMusicNoise() {
        return musicNoise;
    }
    
    public void setMusicNoise(String musicNoise) {
        this.musicNoise = musicNoise;
    }
    
    public String getCleaningFrequency() {
        return cleaningFrequency;
    }
    
    public void setCleaningFrequency(String cleaningFrequency) {
        this.cleaningFrequency = cleaningFrequency;
    }
    
    public String getSocialStyle() {
        return socialStyle;
    }
    
    public void setSocialStyle(String socialStyle) {
        this.socialStyle = socialStyle;
    }
    
    public String getTemperaturePreference() {
        return temperaturePreference;
    }
    
    public void setTemperaturePreference(String temperaturePreference) {
        this.temperaturePreference = temperaturePreference;
    }
    
    public String getLightingPreference() {
        return lightingPreference;
    }
    
    public void setLightingPreference(String lightingPreference) {
        this.lightingPreference = lightingPreference;
    }
    
    public String getPetCompatibility() {
        return petCompatibility;
    }
    
    public void setPetCompatibility(String petCompatibility) {
        this.petCompatibility = petCompatibility;
    }
    
    public String getSmokingPreference() {
        return smokingPreference;
    }
    
    public void setSmokingPreference(String smokingPreference) {
        this.smokingPreference = smokingPreference;
    }
    
    public String getParkingNeeds() {
        return parkingNeeds;
    }
    
    public void setParkingNeeds(String parkingNeeds) {
        this.parkingNeeds = parkingNeeds;
    }
    
    public String getOvernightGuests() {
        return overnightGuests;
    }
    
    public void setOvernightGuests(String overnightGuests) {
        this.overnightGuests = overnightGuests;
    }
    
    public String getWorkFromHome() {
        return workFromHome;
    }
    
    public void setWorkFromHome(String workFromHome) {
        this.workFromHome = workFromHome;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    // Builder pattern implementation
    public static TenantProfileDTOBuilder builder() {
        return new TenantProfileDTOBuilder();
    }
    
    public static class TenantProfileDTOBuilder {
        private Long id;
        private Long userId;
        private String name;
        private String email;
        private String gender;
        private Double budget;
        private String location;
        private Integer cleanlinessLevel;
        private Integer noiseTolerance;
        private Boolean smoking;
        private Boolean pets;
        private Set<String> preferredGenders;
        private String dailySchedule;
        private String guestFrequency;
        private String cookingHabits;
        private String musicNoise;
        private String cleaningFrequency;
        private String socialStyle;
        private String temperaturePreference;
        private String lightingPreference;
        private String petCompatibility;
        private String smokingPreference;
        private String parkingNeeds;
        private String overnightGuests;
        private String workFromHome;
        private String allergies;
        
        public TenantProfileDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public TenantProfileDTOBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public TenantProfileDTOBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public TenantProfileDTOBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public TenantProfileDTOBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }
        
        public TenantProfileDTOBuilder budget(Double budget) {
            this.budget = budget;
            return this;
        }
        
        public TenantProfileDTOBuilder location(String location) {
            this.location = location;
            return this;
        }
        
        public TenantProfileDTOBuilder cleanlinessLevel(Integer cleanlinessLevel) {
            this.cleanlinessLevel = cleanlinessLevel;
            return this;
        }
        
        public TenantProfileDTOBuilder noiseTolerance(Integer noiseTolerance) {
            this.noiseTolerance = noiseTolerance;
            return this;
        }
        
        public TenantProfileDTOBuilder smoking(Boolean smoking) {
            this.smoking = smoking;
            return this;
        }
        
        public TenantProfileDTOBuilder pets(Boolean pets) {
            this.pets = pets;
            return this;
        }
        
        public TenantProfileDTOBuilder preferredGenders(Set<String> preferredGenders) {
            this.preferredGenders = preferredGenders;
            return this;
        }
        
        public TenantProfileDTOBuilder dailySchedule(String dailySchedule) {
            this.dailySchedule = dailySchedule;
            return this;
        }
        
        public TenantProfileDTOBuilder guestFrequency(String guestFrequency) {
            this.guestFrequency = guestFrequency;
            return this;
        }
        
        public TenantProfileDTOBuilder cookingHabits(String cookingHabits) {
            this.cookingHabits = cookingHabits;
            return this;
        }
        
        public TenantProfileDTOBuilder musicNoise(String musicNoise) {
            this.musicNoise = musicNoise;
            return this;
        }
        
        public TenantProfileDTOBuilder cleaningFrequency(String cleaningFrequency) {
            this.cleaningFrequency = cleaningFrequency;
            return this;
        }
        
        public TenantProfileDTOBuilder socialStyle(String socialStyle) {
            this.socialStyle = socialStyle;
            return this;
        }
        
        public TenantProfileDTOBuilder temperaturePreference(String temperaturePreference) {
            this.temperaturePreference = temperaturePreference;
            return this;
        }
        
        public TenantProfileDTOBuilder lightingPreference(String lightingPreference) {
            this.lightingPreference = lightingPreference;
            return this;
        }
        
        public TenantProfileDTOBuilder petCompatibility(String petCompatibility) {
            this.petCompatibility = petCompatibility;
            return this;
        }
        
        public TenantProfileDTOBuilder smokingPreference(String smokingPreference) {
            this.smokingPreference = smokingPreference;
            return this;
        }
        
        public TenantProfileDTOBuilder parkingNeeds(String parkingNeeds) {
            this.parkingNeeds = parkingNeeds;
            return this;
        }
        
        public TenantProfileDTOBuilder overnightGuests(String overnightGuests) {
            this.overnightGuests = overnightGuests;
            return this;
        }
        
        public TenantProfileDTOBuilder workFromHome(String workFromHome) {
            this.workFromHome = workFromHome;
            return this;
        }
        
        public TenantProfileDTOBuilder allergies(String allergies) {
            this.allergies = allergies;
            return this;
        }
        
        public TenantProfileDTO build() {
            return new TenantProfileDTO(id, userId, name, email, gender, budget, location, 
                                      cleanlinessLevel, noiseTolerance, smoking, pets, preferredGenders,
                                      dailySchedule, guestFrequency, cookingHabits, musicNoise,
                                      cleaningFrequency, socialStyle, temperaturePreference, 
                                      lightingPreference, petCompatibility, smokingPreference,
                                      parkingNeeds, overnightGuests, workFromHome, allergies);
        }
    }
} 