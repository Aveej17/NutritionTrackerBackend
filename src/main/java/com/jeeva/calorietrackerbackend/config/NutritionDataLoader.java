package com.jeeva.calorietrackerbackend.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.jeeva.calorietrackerbackend.model.NutritionReference;
import com.jeeva.calorietrackerbackend.repository.NutritionReferenceRepository;

@Component
public class NutritionDataLoader implements CommandLineRunner {

    private final NutritionReferenceRepository repository;

    public NutritionDataLoader(NutritionReferenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Load only if table is empty
        if (repository.count() > 0) {
//            System.out.println("Nutrition table already populated.");
            return;
        }

//        System.out.println("Loading nutrition reference data...");

        ClassPathResource resource = new ClassPathResource("data/nutrition_reference.csv");
        InputStream inputStream = resource.getInputStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                if (firstLine) { // skip header
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",");

                if (fields.length < 6) continue;

                NutritionReference ref = new NutritionReference(
                        fields[0].trim(),
                        Double.parseDouble(fields[1]),
                        Double.parseDouble(fields[2]),
                        Double.parseDouble(fields[3]),
                        Double.parseDouble(fields[4]),
                        Double.parseDouble(fields[5])
                );

                repository.save(ref);
            }
        }

//        System.out.println("Nutrition reference data loaded successfully.");
    }
}
