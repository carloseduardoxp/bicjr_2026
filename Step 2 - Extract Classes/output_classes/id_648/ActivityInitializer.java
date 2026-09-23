package com.openisle.config;

import com.openisle.model.Activity;
import com.openisle.model.ActivityType;
import com.openisle.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityInitializer implements CommandLineRunner {
    private final ActivityRepository activityRepository;

    @Override
    public void run(String... args) {
        if (activityRepository.findByType(ActivityType.MILK_TEA) == null) {
            Activity a = new Activity();
            a.setTitle("建站送奶茶活动");
            a.setType(ActivityType.MILK_TEA);
            activityRepository.save(a);
        }
    }
}
