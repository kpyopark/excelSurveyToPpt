package elevenquest.com.surveyutil;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import java.io.FileInputStream;
import java.io.InputStream;

import elevenquest.com.*;

public class ExcelSurveyLoader {

    public static Survey loadSurvey(String hospital, String period, String fileLocation) throws Exception {
        return loadSurvey(hospital, period, new FileInputStream(fileLocation));
    }    

    public static Survey loadSurvey(String hospital, String period, InputStream is) throws Exception {
        List<ImmutablePair<String, Map<Surveyee, String>>> results = loadSurveyResult(is);
        Survey survey = new Survey(hospital, period);
        results.stream().forEach(value -> {
            SurveyQuestionResult.addSurveyQuestionResult(survey, value.left, value.right) ;
        });
        survey.recalculateSurveyResult();
        return survey;
    }

    public static List<ImmutablePair<String, Map<Surveyee, String>>> loadSurveyResult(InputStream is) throws Exception {
        
        List<ImmutablePair<String, Map<Surveyee, String>>> surveyResults = new ArrayList<ImmutablePair<String, Map<Surveyee, String>>>();
        List<String> questionAndIdentities = new ArrayList<String>();
        InputStream excelFile = null;
        Workbook workbook = null;
    
        try {
            excelFile = is;
            workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            boolean isHeader = true;
            while(iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                Surveyee surveyee = new Surveyee();
                int questionIndex = 0;
                if(isHeader) {
                    // skip 3 cells. timestamp, department, position
                    cellIterator.next();
                    cellIterator.next();
                    cellIterator.next();
                } else {
                    // read survey information.
                    surveyee.timestamp = cellIterator.next().getDateCellValue();
                    surveyee.department = cellIterator.next().getStringCellValue();
                    surveyee.position = cellIterator.next().getStringCellValue();
                }
                while(cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    if(isHeader) {
                        questionAndIdentities.add(currentCell.getStringCellValue());
                        ImmutablePair<String, Map<Surveyee, String>> surveyResult = new ImmutablePair<String, Map<Surveyee, String>>(
                            currentCell.getStringCellValue(),
                            new HashMap<Surveyee, String>()
                        );
                        surveyResults.add(surveyResult);
                    } else {
                        surveyResults.get(questionIndex++).right.put(surveyee, currentCell.getStringCellValue());
                    }
                }
                isHeader = false;
            }
        } finally {
            if(workbook != null) try { workbook.close(); } catch(Exception e1) { e1.printStackTrace();}
        }
        return surveyResults;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("# of args :" + args.length);
        if(args.length < 1) {
            System.out.println("Usage java ExcelSurveyLoader target_file_path.");
            System.exit(1);
        }
        Survey survey = ExcelSurveyLoader.loadSurvey("Smile", "201911", args[0]);
        survey.getSurveyQuestions().entrySet().stream().forEach(value -> System.out.println(value));
    }
}
