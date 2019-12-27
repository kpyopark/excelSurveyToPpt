package elevenquest.com;

import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Survey {

    String name;
    Map<String /* Question Number */, SurveyQuestion> questions;

    public Survey(String name) {
        this.name = name;
        this.questions = new HashMap<String, SurveyQuestion>();
    }

    public String getName() {
        return this.name;
    }

    public Map<String, SurveyQuestion> getSurveyQuestions() {
        return this.questions;
    }

    public SurveyQuestion getSurveyQuestion(String question) {
        ImmutablePair<String, String> mainSubCategory = SurveyQuestion.getCategory(question);
        if(mainSubCategory == null || mainSubCategory.left == null) {
            System.out.println("Main Category is empty. : " + question);
            return null;
        }
        String mainCategory = mainSubCategory.left;
        String subCategory = mainSubCategory.right;
        // 1. xxxxx [target1]
        // 1. xxxxx [target2]
        // 1. xxxxx [target3] --> All results are stored in one Question

        SurveyQuestion registeredQuestion = questions.get(mainCategory);
        if (registeredQuestion == null) {
            registeredQuestion = new SurveyQuestion(question);
            questions.put(mainCategory, registeredQuestion);
            registeredQuestion.addRelatedQuestion(registeredQuestion);
        } 
        if (subCategory != null) {
            if(registeredQuestion.getSubCategoryNumber() == null) {
                System.out.println("This parent question has wrong sub category number. :" + question + ":" + registeredQuestion.getQuestion());
            }
            SurveyQuestion matchedQuestion = registeredQuestion.getRelatedQuestions()
                .stream()
                .filter(surveyQuestion -> subCategory.equals(surveyQuestion.getSubCategoryNumber()))
                .findFirst().orElse(null);
            if(matchedQuestion == null) {
                matchedQuestion = new SurveyQuestion(question);
                registeredQuestion.addRelatedQuestion(matchedQuestion);
            }
            registeredQuestion = matchedQuestion;
        }
        return registeredQuestion;
    }

    public void recalculateSurveyResult() {
        questions.entrySet().stream().forEach(entry -> {
            if(entry.getValue().getSurveyType() == SurveyQuestionType.CHOICE)
                entry.getValue().recalculateStat();
        });
    }
    
}