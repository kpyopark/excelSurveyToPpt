package elevenquest.com.surveyutil;

import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;

import elevenquest.com.Survey;
import elevenquest.com.SurveyQuestion;
import elevenquest.com.SurveyQuestionResult;
import elevenquest.com.SurveyQuestionType;
import elevenquest.com.Surveyee;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.Color;

public class SurveyPowerPointWriter {
    Survey survey;
    PowerPointReportWriter writer;

    public SurveyPowerPointWriter(String location, Survey survey) {
        this.writer = new PowerPointReportWriter(location, survey.getName());
        this.survey = survey;
        this.writer.createPowerPointFile();
    }

    private void addCoverSlide() {
        writer.addSlide(null);
        writer.addPhraseAndTextRun(TextAlign.CENTER,new Rectangle(107,162,507,124), null);
        writer.addTextRun(this.survey.getFormattedPeriod(), 31.0, new Color( 0xff000000));
        writer.addLineBreaker();
        writer.addTextRun("직원간 다면평가 보고서 ", 44.0, new Color( 0xff000000));
        writer.addPhraseAndTextRun(TextAlign.LEFT,new Rectangle(348,338,315,58), null);
        writer.addTextRun("시행기간 : " + this.survey.getFormattedPeriod(), 14.0, new Color( 0xff000000));
        writer.addLineBreaker();
        writer.addTextRun("진행방법 : Mobile Research", 14.0, new Color( 0xff000000));
        writer.addSlide(null);
        writer.addPhraseAndTextRun(TextAlign.CENTER,new Rectangle(107,162,507,153), null);
        writer.addTextRun("다면평가", 36.0, new Color( 0xff0070c0));
        writer.addLineBreaker();
        writer.addTextRun("문항별 직원평가 ", 36.0, new Color( 0xff0070c0));
        writer.addTextRun("Data", 36.0, new Color( 0xff0070c0));
        writer.addTextRun(" ", 36.0, new Color( 0xff0070c0));
    }

    private void addLegend(SurveyQuestion question) {
    	int colCount = 0;
    	int xMargin = 50;
    	int yMargin = 10;
    	int yHeader = 80;
    	int width = 40;
    	int height = 8;
    	int colSpace = 6;
    	for(String legend : question.getAllValidResultSet()) {
    		Rectangle rect = new Rectangle(xMargin + (width + colSpace) * colCount,
                    yHeader + yMargin, width, height); 
    		writer.addPhraseAndTextRun(TextAlign.CENTER, rect, ChoiceBarChart.BAR_SHADOW_COLORS[colCount]);
    		writer.addTextRun("", 5.0, ChoiceBarChart.BAR_SHADOW_COLORS[colCount], ChoiceBarChart.BAR_COLORS[colCount]);
    		Rectangle textRect = new Rectangle(xMargin + (width + colSpace) * colCount,
                    yHeader + yMargin + height, width, height);
    		writer.addPhraseAndTextRun(TextAlign.CENTER, textRect, null);
    		writer.addTextRun(legend, 5.0, ChoiceBarChart.BAR_SHADOW_COLORS[colCount]);
    		colCount++;
    	}
    }

    private void addChoiceQuestionSlide(SurveyQuestion question) throws IOException {
        String questionText = question.getQuestion();
        boolean needSlide = true;
        int rowCount = 0;
        int colCount = 0;
        int xMargin = 40;
        int yMargin = 30;
        int yHeader = 80;
        int maxX = 720;
        int maxY = 550;
        int height = 100;
        int width = 120;
        int textHeight = 17;
        int maxRow = (maxY - yHeader - 2 * yMargin) / height;
        int maxCol = (maxX - 2 * xMargin) / width;
        int rowSpace = ((maxY - yHeader - 2 * yMargin) - maxRow * height) / (maxRow - 1);
        int colSpace = ((maxX - 2 * xMargin) - maxCol * width) / (maxCol - 1);
        for(SurveyQuestionResult result: question.getSurveyQuestionResult()) {
            if(needSlide) {
            	needSlide = false;
                writer.addSlide(null);
                addLegend(question);
                writer.addPhraseAndTextRun(TextAlign.CENTER, new Rectangle(40, 36, 640, 46), null);
                writer.addTextRun(questionText, 16.0, new Color(0xff000000));
            }
            Rectangle rect = new Rectangle(xMargin + (width + colSpace) * colCount,
                yHeader + yMargin + (height + rowSpace) * rowCount, 
                width, 
                height
            );
            writer.addPicture(rect, new ChoiceBarChart(result, question.getMaxStatCount()).exportAsPNG());
            Rectangle textRect = new Rectangle(rect.x, rect.y + height - textHeight + 3
            		, width, textHeight);
            writer.addPhraseAndTextRun(TextAlign.CENTER, textRect, null);
            writer.addTextRun(result.getTargetIdentity().trim()
            		, 9.0, Color.BLACK, Color.WHITE);
            String dislikePeople = result.getSurveyeeStringFromResult("아니다");
            List<Surveyee> dislikePeopleList = result.getSurveyeeListFromResult("아니다");
            if(dislikePeople != null) {
            	Rectangle dislikeRect = new Rectangle(xMargin + 28 + (width + colSpace) * colCount,
                        yHeader + yMargin + 5 + (height + rowSpace) * rowCount, 
                        width - 35, 
                        13 * dislikePeopleList.size()
                    ); 
            	writer.addPhraseAndTextRun(TextAlign.LEFT, dislikeRect, ChoiceBarChart.BAR_COLOR3);
            	writer.addTextRun(dislikePeople
                		, 7.0, ChoiceBarChart.BAR_COLOR3);
            }
            colCount++;
            if(colCount >= maxCol) {
                colCount = 0;
                rowCount++;
            }
            if(rowCount >= maxRow) {
                rowCount = 0;
                needSlide = true;
            }
        }
    }
    
    private void addTableHeaderRowInTextQuestionSlide(SurveyQuestion question) {
        writer.addSlide(null);
        writer.addPhraseAndTextRun(TextAlign.CENTER, new Rectangle(40, 36, 640, 46), null);
        writer.addTextRun(question.getQuestion(), 16.0, new Color(0xff000000));
        writer.addTable(new Rectangle(33, 109, 658, 197));
        // Header
        writer.addRow(32.32496062992126);
        writer.addCell(84.37566929133858, Color.BLACK, new Color(163,198,215));
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("부서 ", 10.0, new Color(0xff000000));
        writer.addCell(50.62535433070866, Color.BLACK, new Color(163,198,215));
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("직급", 10.0, new Color(0xff000000));
        writer.addCell(101.25070866141732, Color.BLACK, new Color(163,198,215));
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("대상", 10.0, new Color(0xff000000));
        writer.addCell(421.87795275590554, Color.BLACK, new Color(163,198,215));
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("내용", 10.0, new Color(0xff000000));
    }

    private void addTextQuestionSlide(SurveyQuestion question) {
        // TODO :
        List<SurveyQuestion> relatedQuestions = question.getRelatedQuestions();
        SurveyQuestionResult baseResult = relatedQuestions.get(0).getSurveyQuestionResult().get(0);
        int maxX = 720;
        int maxY = 550;
        int yMargin = 30;
        int yHeader = 80;
        int rowCount = 0;
        int rowHeight = 40;
        boolean needSlide = true;
        for(Surveyee surveyee : baseResult.getSurveyResult().keySet()) {
        	if(SurveyQuestionResult.isProperTargetIdentity(baseResult.getSurveyResult().get(surveyee))) {
        		if(needSlide) {
        			needSlide = false;
        	    	addTableHeaderRowInTextQuestionSlide(question);
        		}
                writer.addRow(rowHeight);
                writer.addCell(84.37566929133858);
                writer.addParagraphToCell(TextAlign.LEFT);
                writer.addTextRun(surveyee.department, 10.0, new Color(0xff000000));
                writer.addCell(50.62535433070866);
                writer.addParagraphToCell(TextAlign.LEFT);
                writer.addTextRun(surveyee.position, 10.0, new Color(0xff000000));
                writer.addCell(101.25070866141732);
                writer.addParagraphToCell(TextAlign.LEFT);
                writer.addTextRun(baseResult.getSurveyResult().get(surveyee), 10.0, new Color(0xff000000));
                writer.addCell(421.87795275590554);
                writer.addParagraphToCell(TextAlign.LEFT);
                writer.addTextRun(relatedQuestions.get(1).getSurveyQuestionResult().get(0).getSurveyResult().get(surveyee)
                		, 10.0, new Color(0xff000000));
        	}
        	rowCount++;
        	if(rowCount * rowHeight > (maxY - yMargin - yHeader) ) {
        		rowCount = 0;
        		needSlide = true;
        	}
        }
    }

    public void writeSurveyReport() throws IOException {
        this.addCoverSlide();
        for(String key : this.survey.getSurveyQuestions().keySet()) {
            SurveyQuestion question = this.survey.getSurveyQuestions().get(key);
            if(question.getSurveyType() == SurveyQuestionType.CHOICE) {
                this.addChoiceQuestionSlide(question);
            } else {
            	this.addTextQuestionSlide(question);
            }
        }
        this.writer.writePowerPointFile();
    }
    
    public static void convertExcelToPowerpoint(String hospital, String period, String excelLocation, String destination) {
    	try {
            Survey survey = ExcelSurveyLoader.loadSurvey(hospital, period, excelLocation);
            SurveyPowerPointWriter surveyWriter = new SurveyPowerPointWriter(destination, survey);
            surveyWriter.writeSurveyReport();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void main(String[] args) throws Exception {
    	convertExcelToPowerpoint("Smile", "201911", args[0], args[1]);
    }
}