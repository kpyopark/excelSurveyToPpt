package elevenquest.com;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;


public class SurveyQuestion {

    private SurveyQuestionType surveyType;
    private String question;
    private String mainCategoryNumber; // 3-1. xxxx --> 3
    private String subCatgoryNumber; // 3-1. xxxx --> 1
    /**
     * This attribute for the TEXT type question. Generally TEXT type questions has siblings in the same survey form. 
     * For example. 
     * 3-1. Who is the best guy in this hospital ?
     * 3-2. What is the his strogest doings in this month ?
     */
    private final List<SurveyQuestion> relatedQuestions = new ArrayList<SurveyQuestion>();
    private List<SurveyQuestionResult> results = new ArrayList<SurveyQuestionResult>();

    /** 
     * The below attribute is designed only for CHOICE type.
     * CHOICE type can have vairous result set. 
     * But, we can determine the full result set from on survey result. 
     * So, we should look around the overall result set for the same question. 
     * */ 
    Set<String> validResultSet = new HashSet<String>();
    
    private void makeEnumerationResultSet(final Set<String> distinctValues) {
        validResultSet.addAll(distinctValues);
    }

    public void recalculateStat() {
        for (final SurveyQuestionResult result : this.results) {
            Set<String> validResultSetDup = new HashSet<String>();
            validResultSetDup.addAll(validResultSet);
            validResultSetDup.removeAll(result.getSurveyResult().values());
            validResultSetDup.stream().forEach(unregisteredValue -> {
                // result.getSurveyResult().
                result.addEmptyStatLable(unregisteredValue);
            });
        }
    }
    

    SurveyQuestion(final String question) {
        // This constructor will be called in Survey class. (getSurveyQuestion(...))
        this.question = question;
        ImmutablePair<String, String> mainSubCategory = getCategory(question);
        this.mainCategoryNumber = mainSubCategory.left;
        this.subCatgoryNumber = mainSubCategory.right;
    }

    public static ImmutablePair<String, String> getCategory(String question) {
        String mainCategory;
        String subCategory;
        if(question == null) return null;
        if(question.length() < 3) return null;
        if(question.indexOf(".") < 1) return null;
        if(question.indexOf("-") < 1) {
            // There is no sub category number in the question.
            // such like "3. The below people doesn't like the job ?"
            mainCategory = question.substring(0, question.indexOf("."));
            subCategory = null;
        } else {
            // There is a sub category number
            // suche like "3-1."
            mainCategory = question.substring(0, question.indexOf("-")+1);
            subCategory = question.substring(mainCategory.length(),
                question.indexOf("."));
        }
        return new ImmutablePair<String, String>(mainCategory, subCategory);
    }

    public String getMainCategoryNumber() {
        return this.mainCategoryNumber;
    }

    public String getSubCategoryNumber() {
        return this.subCatgoryNumber;
    }

    public void setSurveyType(final SurveyQuestionType type) {
        this.surveyType = type;
    }

    public void addRelatedQuestion(final SurveyQuestion relatedQuestion) {
        this.relatedQuestions.add(relatedQuestion);
    }

    public void addResult(final SurveyQuestionResult result) {
        this.results.add(result);
        makeEnumerationResultSet(
            result.getSurveyResult()
            .values()
            .stream()
            .collect(Collectors.toSet())
        );
    }

    public SurveyQuestionType getSurveyType() {
        return this.surveyType;
    }

    public String getQuestion() {
        return this.question;
    }

    public List<SurveyQuestion> getRelatedQuestions() {
        return this.relatedQuestions;
    }

    public List<SurveyQuestionResult> getSurveyQuestionResult() {
        return this.results;
    }

    @Override
    public String toString() {
        return "SurveyQuestion [mainCategoryNumber=" + mainCategoryNumber + ", question=" + question
                + ", relatedQuestions=" + relatedQuestions.stream().filter(relatedOne -> relatedOne != this).collect(Collectors.toList()) + ", results=" + results + ", subCatgoryNumber="
                + subCatgoryNumber + ", surveyType=" + surveyType + ", validResultSet=" + validResultSet + "]";
    }



}