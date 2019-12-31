package elevenquest.com.surveyutil;

import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;

import elevenquest.com.Survey;
import elevenquest.com.SurveyQuestion;
import elevenquest.com.SurveyQuestionResult;
import elevenquest.com.SurveyQuestionType;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    private void addMarkableResult(String mark) {
        writer.addPhraseAndTextRun(TextAlign.CENTER, new Rectangle(55, 150, 75, 97), null);
        writer.setLineColor(new Color(0xffc00000));
        writer.addTextRun("응답 : 홍길동", 7.0, new Color(0xff000000));
    }

    private void addChoiceQuestionSlide(SurveyQuestion question) throws IOException {
        String questionText = question.getQuestion();
        int rowCount = 0;
        int colCount = 0;
        int xMargin = 40;
        int yMargin = 30;
        int yHeader = 80;
        int maxX = 640;
        int maxY = 480;
        int height = 50;
        int width = 40;
        int maxRow = (maxY - yHeader - 2 * yMargin) / height;
        int maxCol = (maxX - 2 * xMargin) / width;
        int rowSpace = ((maxY - yHeader - 2 * yMargin) - maxRow * height) / (maxRow - 1);
        int colSpace = ((maxX - 2 * xMargin) - maxCol * width) / (maxCol - 1);
        for(SurveyQuestionResult result: question.getSurveyQuestionResult()) {
            if(rowCount == 0) {
                writer.addSlide(null);
                writer.addPhraseAndTextRun(TextAlign.CENTER, new Rectangle(40, 36, 640, 46), null);
                writer.addTextRun(questionText, 16.0, new Color(0xff000000));
            }
            Rectangle rect = new Rectangle(xMargin + (width + colSpace) * colCount++,
                yHeader + yMargin + (height + rowSpace) * rowCount++, 
                width, 
                height
            );
            writer.addPicture(rect, new ChoiceBarChart(result).exportAsPNG());
            if(colCount >= maxCol) {
                colCount = 0;
            }
            if(rowCount >= maxRow) {
                rowCount = 0;
            }
        }
    }

    private void addTextQuestionSlide() {
        writer.addSlide(null);
        writer.addPhraseAndTextRun(TextAlign.CENTER, new Rectangle(40, 36, 640, 46), null);
        writer.addTextRun("2-1. Question", 16.0, new Color(0xff000000));
        writer.addTable(new Rectangle(33, 109, 658, 197));
        // Header
        writer.addRow(32.32496062992126);
        writer.addCell(84.37566929133858);
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("부서 ", 10.0, new Color(0xff000000));
        writer.addCell(50.62535433070866);
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("직급", 10.0, new Color(0xff000000));
        writer.addCell(101.25070866141732);
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("대상", 10.0, new Color(0xff000000));
        writer.addCell(421.87795275590554);
        writer.addParagraphToCell(TextAlign.CENTER);
        writer.addTextRun("내용", 10.0, new Color(0xff000000));
        // Contents
        writer.addRow(32.32496062992126);
        writer.addCell(84.37566929133858);
        writer.addParagraphToCell(TextAlign.LEFT);
        writer.addTextRun("Department", 10.0, new Color(0xff000000));
        writer.addCell(50.62535433070866);
        writer.addParagraphToCell(TextAlign.LEFT);
        writer.addTextRun("Position", 10.0, new Color(0xff000000));
        writer.addCell(101.25070866141732);
        writer.addParagraphToCell(TextAlign.LEFT);
        writer.addTextRun("Target", 10.0, new Color(0xff000000));
        writer.addCell(421.87795275590554);
        writer.addParagraphToCell(TextAlign.LEFT);
        writer.addTextRun("Contents", 10.0, new Color(0xff000000));
        // Repeat
    }

    public void writeSurveyReport() throws IOException {
        this.addCoverSlide();
        for(String key : this.survey.getSurveyQuestions().keySet()) {
            SurveyQuestion question = this.survey.getSurveyQuestions().get(key);
            if(question.getSurveyType() == SurveyQuestionType.CHOICE) {
                this.addChoiceQuestionSlide(question);
            } else {
                // TODO :
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Survey survey = ExcelSurveyLoader.loadSurvey("Smile", "201911", args[0]);
        SurveyPowerPointWriter surveyWriter = new SurveyPowerPointWriter(args[1], survey);
        surveyWriter.writeSurveyReport();
    }
}