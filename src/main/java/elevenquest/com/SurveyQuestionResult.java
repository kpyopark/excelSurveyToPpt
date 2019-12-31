package elevenquest.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyQuestionResult {
    
    private String targetIdentity;  // personal position & name
    private SurveyQuestion surveyQuestion;
    /**
     * This attributes contains real result value with surveyee. for example. Excellent, Great, Normal, I don't know
     *  */ 
    private Map<Surveyee, String> surveyResult = new HashMap<Surveyee, String>();
    private Map<String, Integer> surveyStatResult = new HashMap<String, Integer>();

    private static String[] IGNORABLE_RESULT = {"없", "모름", "모르", ".", "-"};

    public SurveyQuestionResult(SurveyQuestion surveyQuestion, String identity) {
        this.targetIdentity = identity;
        this.surveyQuestion = surveyQuestion;
    }

    public static void addSurveyQuestionResult(Survey survey, String questionAndIdentity, Map<Surveyee, String> surveyResult) {
        int identityStartPosition = questionAndIdentity.indexOf("[", 1);
        String question;
        String targetIdentity;
        SurveyQuestionType type;
        if (identityStartPosition > 1) {
            question = questionAndIdentity.substring(0, identityStartPosition);
            targetIdentity = questionAndIdentity.substring(identityStartPosition+1, questionAndIdentity.indexOf("]", identityStartPosition));
            type = SurveyQuestionType.CHOICE;
        } else {
            question = questionAndIdentity;
            targetIdentity = null;
            type = SurveyQuestionType.TEXT;
        }
        SurveyQuestion surveyQuestion = survey.getSurveyQuestionFromText(question);
        if (surveyQuestion == null) {
            System.out.println("This question has no category number. We can't process this question item.[" + question + "]");
            return;
        }
        surveyQuestion.setSurveyType(type);
        SurveyQuestionResult result = new SurveyQuestionResult(surveyQuestion, targetIdentity);
        surveyResult.entrySet().stream().forEach(entry -> {
            result.addSurveyResultRecord(entry.getKey(), entry.getValue());
        });
        surveyQuestion.addResult(result);
    }

    public List<String> getProperTextResults() {
        List<String> surveyTextResults = new ArrayList<String>();
        this.surveyResult.forEach((key, value) -> { 
            boolean isIgnorableResult = false;
            for(String word : IGNORABLE_RESULT) {
                if (!isIgnorableResult && value.contains(word)) {
                    isIgnorableResult = true;
                    break;
                }
            }
        });
        return surveyTextResults;
    }

    public void setTargetIdentity(String targetIdentity) {
        this.targetIdentity = targetIdentity;
    }

    public String getTargetIdentity() {
        return this.targetIdentity;
    }

    public Map<Surveyee, String> getSurveyResult() {
        return this.surveyResult;
    }

    public void addEmptyStatLable(String label) {
        this.surveyStatResult.put(label, 0);
    }
    
    private void addSurveyResultRecord(Surveyee surveyee, String result) {
        this.surveyResult.put(surveyee, result);
        if (this.surveyQuestion.getSurveyType() == SurveyQuestionType.CHOICE) {
            Integer currentSum = surveyStatResult.get(result);
            if (currentSum == null) currentSum = 0;
            currentSum++;
            surveyStatResult.put(result, currentSum);
        }
    }

    public Map<String, Integer> getSurveyStatResult() {
        return this.surveyStatResult;
    }

    @Override
    public String toString() {
        return "SurveyQuestionResult [surveyResult=" + surveyResult
                + ", surveyStatResult=" + surveyStatResult + ", targetIdentity=" + targetIdentity + "]";
    }



}