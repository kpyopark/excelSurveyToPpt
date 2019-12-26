package elevenquest.com;

import java.util.*;


public class SurveyItem {

    private SurveyItemType surveyType;
    private String question;
    private String targetIdentity;  // personal position & name

    public static SurveyItem makeSurveyItem(Map<Surveyee, String> surveyResult, String questionAndIdentity) {
        SurveyItem item = new SurveyItem();
        if(questionAndIdentity.contains("[")) {
            item.setQuestion(questionAndIdentity.substring(0, questionAndIdentity.indexOf("[", 1)));
            item.setTargetIdentity(questionAndIdentity.substring(questionAndIdentity.indexOf("[", 1)+1, 
                questionAndIdentity.indexOf("]", questionAndIdentity.indexOf("[", 1)+1))
            );
            item.setSurveyType(SurveyItemType.CHOICE);
        } else {
            item.setQuestion(questionAndIdentity);
            item.setSurveyType(SurveyItemType.TEXT);
        }
        surveyResult.forEach((key, value) -> item.addSurvey(key, value));
        return item;
    }

    /*
    @Deprecated
    private SurveyItemType determineType(List<String> results) {
        boolean hasIgnorableResult = false;
        for(String ignorableWord: IGNORABLE_RESULT) {
            hasIgnorableResult = results.stream().anyMatch(value -> value.contains(ignorableWord));
            if(hasIgnorableResult)
                break;
        }
        return (!hasIgnorableResult && results.stream().distinct().count() <= 5) ? SurveyItemType.CHOICE : SurveyItemType.TEXT;
    }    
    */
    
    /**
     * This attributes contains real result value with surveyee. for example. Excellent, Great, Normal, I don't know
     *  */ 
    private Map<Surveyee, String> surveyResult = new HashMap<Surveyee, String>();
    
    /** This attribute contains the count of specific result. for example, 5 # of counts in "Excellent". */    
    private Map<String, Integer> surveyStatResult = new HashMap<String, Integer>();

    /**
     * This attribute contains the specific result for individual opinions. for example. 
     * In surveyResult, it indicates the target individual - Kim sae hi, 
     * In surveyRelatedResult, it contains "Dr. sae hi is great for helping other Drs."
     */
    private Map<Surveyee, String> surveyRelatedResult = new HashMap<Surveyee, String>();
    
    private static String[] IGNORABLE_RESULT = {"없", "모름", "모르", ".", "-"};

    public void setSurveyType(SurveyItemType type) {
        this.surveyType = type;
    }

    public void setQuestion(String question) {
        this.question = question;        
    }

    public void setTargetIdentity(String targetIdentity) {
        this.targetIdentity = targetIdentity;
    }

    public void addSurvey(Surveyee surveyee, String result) {
        this.surveyResult.put(surveyee, result);
        if (surveyType == SurveyItemType.CHOICE) {
            Integer currentSum = surveyStatResult.get(result);
            if (currentSum == null) currentSum = 0;
            currentSum++;
            surveyStatResult.put(result, currentSum);
        }
    }

    public void addRelatedSurvey(Surveyee surveyee, String relatedResult) {
        this.surveyResult.put(surveyee, relatedResult);
    }

    public SurveyItemType getSurveyType() {
        return this.surveyType;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getTargetIdentity() {
        return this.targetIdentity;
    }

    public List<Surveyee> getSurveyeesFromResult(String result) {
        List<Surveyee> surveyees = new ArrayList<Surveyee>();
        this.surveyResult.forEach((key,value) -> {if (value.equals(result)) { surveyees.add(key); }});
        return surveyees;
    }

    public List<String> getProperRelatedResults() {
        List<String> surveyTextResults = new ArrayList<String>();
        this.surveyResult.forEach((key, value) -> { 
            boolean isIgnorableResult = false;
            for(String word : IGNORABLE_RESULT) {
                if (!isIgnorableResult && value.contains(word)) {
                    isIgnorableResult = true;
                    break;
                }
            }
            if (!isIgnorableResult && value != null && value.length() > 3) {
                String surveyResultStatement = "(" + this.surveyResult.get(key) + ") " + this.surveyRelatedResult.get(key);
                surveyTextResults.add(surveyResultStatement);
            }
        });
        return surveyTextResults;
    }

}