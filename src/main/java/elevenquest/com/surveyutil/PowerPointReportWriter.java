package elevenquest.com.surveyutil;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.awt.Rectangle;
import java.awt.Color;

public class PowerPointReportWriter {

       // PowerPoint File Location
       String destinationFile;
       XMLSlideShow target;
       XSLFSlideMaster defaultMaster;
       XSLFSlide activeSlide;

       public PowerPointReportWriter(String destination, String title) {
              this.destinationFile = destination + File.separator + title + ".pptx";
       }

       public void createPowerPointFile() {
              this.target = new XMLSlideShow();
       }

       public void addSlide(SlideLayout layout) {
              if (this.defaultMaster == null) {
                     this.defaultMaster = target.getSlideMasters().get(0);
              }
              XSLFSlideLayout xslflayout = null;
              if (layout == null) {
                     this.activeSlide = this.target.createSlide();
              } else {
                     xslflayout = this.defaultMaster.getLayout(layout);
                     this.activeSlide = this.target.createSlide(xslflayout);
              }
       }

       public void writePowerPointFile() {
              FileOutputStream out = null;
              try {
                     out = new FileOutputStream(this.destinationFile);
                     this.target.write(out);
              } catch (Exception e) {
                     e.printStackTrace();
              } finally {
                     if (out != null)
                            try {
                                   out.close();
                            } catch (Exception e) {
                            }
              }
       }

       XSLFTextBox shape;
       XSLFTextParagraph paragraph;

       public void addPhraseAndTextRun(TextAlign align, Rectangle textAreaRect, Color lineColor) {
              this.shape = this.activeSlide.createTextBox();
              this.shape.setAnchor(textAreaRect);
              this.shape.clearText();
              this.paragraph = this.shape.addNewTextParagraph();
              this.paragraph.setTextAlign(align);
              if (lineColor != null)
                     this.shape.setLineColor(lineColor);
       }
       
       public void addTextRun(String text, Double font, Color foreColor, Color backColor) {
           XSLFTextRun textRun = this.paragraph.addNewTextRun();
           textRun.setText(text);
           textRun.setFontColor(foreColor);
           textRun.setFontSize(font);
           if(backColor != null) {
        	   this.shape.setFillColor(backColor);
           }
       }

       public void addTextRun(String text, Double font, Color foreColor) {
    	   addTextRun(text, font, foreColor, null);
       }

       public void addLineBreaker() {
              this.paragraph.addLineBreak();
       }

       public void setLineColor(Color color) {
              this.shape.setLineColor(color);
       }

       XSLFTable table;
       XSLFTableRow activeRow = null;
       XSLFTableCell activeCell = null;
       int activeCol = 0;

       public void addTable(Rectangle rect) {
              this.table = activeSlide.createTable();
              this.table.setAnchor(rect);
       }

       public void addRow(double height) {
              this.activeRow = this.table.addRow();
              this.activeRow.setHeight(height);
              this.activeCol = 0;
       }

       public void addCell(double width) {
              this.activeCell = this.activeRow.addCell();
              this.table.setColumnWidth(this.activeCol++, width);
       }

       public void addParagraphToCell(TextAlign align) {
              this.paragraph = this.activeCell.addNewTextParagraph();
              this.paragraph.setTextAlign(align);
       }

       public void addPicture(Rectangle rect, byte[] picture) {
              XSLFPictureData pd = this.target.addPicture(picture, XSLFPictureData.PictureType.PNG);
              XSLFPictureShape pic = this.activeSlide.createPicture(pd);
              pic.setAnchor(rect);
       }

       public static void main(String[] args) {
              if (args.length < 1) {
                     System.out.println("Usage : java PowerPointWriter fileLocation");
                     System.exit(1);
              }
              PowerPointReportWriter writer = new PowerPointReportWriter(args[0], "Report");
              writer.createPowerPointFile();
              writer.writeHeadSlide(writer);
              writer.writeSlide1(writer);
              writer.writeSlide5(writer);
              writer.writePowerPointFile();
       }

       private void writeHeadSlide(PowerPointReportWriter writer) {
              writer.addSlide(null);
              writer.addPhraseAndTextRun(TextAlign.CENTER, new Rectangle(107, 162, 507, 124), null);
              writer.addTextRun("2019. 11", 31.0, new Color(0xff000000));
              writer.addTextRun("", 44.0, new Color(0xff000000));
              writer.addTextRun("\n", 44.0, new Color(0xff000000));
              writer.addTextRun("직원간 다면평가 보고서 ", 44.0, new Color(0xff000000));
              writer.addPhraseAndTextRun(TextAlign.LEFT, new Rectangle(348, 338, 315, 58), null);
              writer.addTextRun("시행기간", 14.0, new Color(0xff000000));
              writer.addTextRun(" : 2019", 14.0, new Color(0xff000000));
              writer.addTextRun("년 ", 14.0, new Color(0xff000000));
              writer.addTextRun("11", 14.0, new Color(0xff000000));
              writer.addTextRun("월 ", 14.0, new Color(0xff000000));
              writer.addTextRun("28", 14.0, new Color(0xff000000));
              writer.addTextRun("일 ", 14.0, new Color(0xff000000));
              writer.addTextRun("∼", 14.0, new Color(0xff000000));
              writer.addTextRun(" 12", 14.0, new Color(0xff000000));
              writer.addTextRun("월 ", 14.0, new Color(0xff000000));
              writer.addTextRun("3", 14.0, new Color(0xff000000));
              writer.addTextRun("일", 14.0, new Color(0xff000000));
              writer.addLineBreaker();
              writer.addTextRun("진행방법 ", 14.0, new Color(0xff000000));
              writer.addTextRun(": Mobile", 14.0, new Color(0xff000000));
              writer.addTextRun(" ", 14.0, new Color(0xff000000));
              writer.addTextRun("Research", 14.0, new Color(0xff000000));
              writer.addPhraseAndTextRun(TextAlign.RIGHT, new Rectangle(516, 500, 168, 28), null);
              writer.addTextRun("1", 12.0, new Color(0xff000000));
       }

       private void writeSlide1(PowerPointReportWriter writer) {
              writer.addSlide(null);
              writer.addPhraseAndTextRun(TextAlign.CENTER, new Rectangle(107, 162, 507, 153), null);
              writer.addTextRun("다면평가", 36.0, new Color(0xff0070c0));
              writer.addLineBreaker();
              writer.addTextRun("문항별 직원평가 ", 36.0, new Color(0xff0070c0));
              writer.addTextRun("Data", 36.0, new Color(0xff0070c0));
              writer.addTextRun(" ", 36.0, new Color(0xff0070c0));
       }

       private void writeSlide5(PowerPointReportWriter writer) {
       }

}