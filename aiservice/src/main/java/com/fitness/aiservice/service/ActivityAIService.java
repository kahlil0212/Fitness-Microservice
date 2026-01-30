package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {

        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswers(prompt);

        log.info("AI Response: {}", aiResponse);



        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {

        //TODO: Build out custom objects to map to ai json response
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    //These lines remove the json markdown generated in ai response
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            //log.info("Parsed Response from AI : {}", jsonContent);

            JsonNode recommendationJson = objectMapper.readTree(jsonContent);
            JsonNode analysisNode = recommendationJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            buildAnalysis(fullAnalysis,analysisNode,"overall","Overall: ");
            buildAnalysis(fullAnalysis,analysisNode,"pace","Pace: ");
            buildAnalysis(fullAnalysis,analysisNode,"heartRate","Heart Rate: ");
            buildAnalysis(fullAnalysis,analysisNode,"caloriesBurned","Calories Burned: ");

            List<String> improvements = buildImprovements(recommendationJson.path("improvements"));
            List<String> suggestions = buildSuggestions(recommendationJson.path("suggestions"));
            List<String> safety = buildSafetyGuidelines(recommendationJson.path("safety"));


            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getActivityType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .build();


        } catch (Exception e) {
            log.error(e.getMessage());
            //TODO: May create custom exception and handle this downstream instead of fallback
            return createFallbackRecommendation(activity);
        }

    }

    /**
     * Fallback method if there was an issue with generating recommendation
     * @param activity
     * @return
     */
    private Recommendation createFallbackRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getActivityType())
                .recommendation("Unable to generate AI recommendation at this time")
                .improvements(Collections.singletonList("Continue with your current workout"))
                .suggestions(Collections.singletonList("Consider consulting with a professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"))
                .build();
    }

    private List<String> buildSafetyGuidelines(JsonNode safetyGuidelines) {
        List<String> safetyGuidelinesList = new ArrayList<>();
        if(safetyGuidelines.isArray()){
            safetyGuidelines.forEach(safetyGuideline -> safetyGuidelinesList.add(safetyGuideline.asText()));
        }
        return safetyGuidelinesList.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines") : safetyGuidelinesList;
    }

    private List<String> buildSuggestions(JsonNode suggestions) {
        List<String> suggestionList = new ArrayList<>();
        if(suggestions.isArray()){
            suggestions.forEach(improvement->{
                String workout = improvement.path("workout").asText();
                String description = improvement.path("description").asText();
                suggestionList.add(String.format("%s: %s",workout,description));

            });
        }
        return suggestionList.isEmpty() ?
                Collections.singletonList("No suggestions provided") : suggestionList;
    }

    private List<String> buildImprovements(JsonNode improvements) {

        List<String> improvementList = new ArrayList<>();
        if(improvements.isArray()){
            improvements.forEach(improvement->{
                String area = improvement.path("area").asText();
                String detail = improvement.path("detail").asText();
                improvementList.add(String.format("%s: %s",area,detail));

            });
        }
        return improvementList.isEmpty() ?
                Collections.singletonList("No improvements provided") : improvementList;
    }

    private void buildAnalysis(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        //if path does not exist it will return missing node versus a null pointer as get would
        if(!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {

        return String.format("""
                Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
                {
                    "analysis": {
                        "overall": "Overall analysis here",
                        "pace": "Pace analysis here",
                        "heartRate": "Heart rate analysis here",
                        "caloriesBurned": "Calories analysis here",
                    },
                    "improvements": [
                        {
                            "area": "Area name",
                            "recommendation": Detailed recommendation"
                        }
                    ],
                    "suggestions": [
                        {
                            "workout": Workout name",
                            "description": "Detailed workout description"
                        }
                    ],
                    "safety": [
                    "Safety point 1",
                    "Safety point 2"
                    ]
                }
                
                Analyze this activity:
                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s
                
                Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                Ensure the response follows the EXACT JSON format shown above.
                """,
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());

    }
}
