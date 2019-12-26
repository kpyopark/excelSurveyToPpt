package elevenquest.com.surveyutil;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import java.io.FileInputStream;
import java.io.File;

import elevenquest.com.*;

public class ExcelSurveyLoader {

    public ExcelSurveyLoader() {

    }

    public static List<ImmutablePair<String, Map<Surveyee, String>>> loadSurveyResult() throws Exception {
        
        List<ImmutablePair<String, Map<Surveyee, String>>> surveyResults = new ArrayList<ImmutablePair<String, Map<Surveyee, String>>>();
        List<String> questionAndIdentities = new ArrayList<String>();
        String filelocation = "/Users/kyunp/Documents/Private/2019.CS.CRM/2019.11.11_Smile_Dental.xlsx";
        FileInputStream excelFile = null;
        Workbook workbook = null;
    
        try {
            excelFile = new FileInputStream(new File(filelocation));
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
                    // make survey information.
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
        List<ImmutablePair<String, Map<Surveyee, String>>> results = loadSurveyResult();
        Survey survey = new Survey("201911.Smile");
        results.stream().forEach(value -> survey.addSurveyItemResult(value.left, value.right));
        System.out.println(survey);
    }
}
