package elevenquest.com;

import java.util.*;

public class Survey {

    String name;
    List<SurveyItem> items;

    public Survey(String name) {
        this.name = name;
        items = new ArrayList<SurveyItem>();
    }

    public void addSurveyItemResult(String question, Map<Surveyee, String> surveyItemResult) {
        items.add(SurveyItem.makeSurveyItem(surveyItemResult, question));
    }

    public List<SurveyItem> getItems() {
        return items;
    }
}