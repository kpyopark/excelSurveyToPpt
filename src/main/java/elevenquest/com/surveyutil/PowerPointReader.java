package elevenquest.com.surveyutil;

import org.apache.poi.xslf.usermodel.XMLSlideShow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
// import org.apache.poi.ooxml.POIXMLProperties.CoreProperties;
import org.apache.poi.xslf.usermodel.*;

public class PowerPointReader {

    String sourceLocation;
    String targetLocation;
    FileInputStream inputStream;
    PrintWriter fos;
    XMLSlideShow ppt = null;

    public PowerPointReader(String location, String targetLocation) {
        this.sourceLocation = location;
        this.targetLocation = targetLocation;
    }

    public void readPowerPoint() {
		try {
            inputStream = new FileInputStream(this.sourceLocation);
            fos = new PrintWriter(new FileOutputStream(this.targetLocation));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		try {
            ppt = new XMLSlideShow(inputStream);
            readPPT();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
            if( ppt != null) try { ppt.close(); } catch(Exception e1) {};
            if( fos != null) try { fos.close(); } catch(Exception e2) {};
        }
    }

    public static String rectToString(java.awt.geom.Rectangle2D rect) {
        return "new Rectangle(" + Math.round(rect.getX()*100)/100 + "," 
            + Math.round(rect.getY()*100)/100 + ","
            + Math.round(rect.getWidth()* 100)/100 + ","
            + Math.round(rect.getHeight()*100)/100 + ")";
    }

    public static String colorToString(java.awt.Color color) {
        return color == null ? "null" : "new Color( 0x" + Integer.toHexString(color.getRGB()) + ")";
    }

    public static String backSlash(String target) {
        return (target.equals("\n")) ? "\\n" : target;
    }

    public void makeTextCodes(XSLFTextShape shape, List<XSLFTextParagraph> paragraphs) {
        for(XSLFTextParagraph paragraph : paragraphs) {
            printCode("        writer.addParagraphToCell(TextAlign." + paragraph.getTextAlign() + ");");
            for(XSLFTextRun textRun : paragraph.getTextRuns()) {
                String text = textRun.getRawText();
                printCode("        writer.addTextRun(\"" + backSlash(text) + "\", " + textRun.getFontSize() + ", " + colorToString(((SolidPaint)textRun.getFontColor()).getSolidColor().getColor()) + ");");
                //printCode("TextRun: Text[" + text + "] fontSize[" + textRun.getFontSize() + "] Color [" + textRun.getFontColor() + "]");
            }
        }
    }

    public void printCode(String code) {
        fos.println(code);
    }

	public static void main(String[] args) {
		
        String fileName;
        String targetFileName;
		
		if (args.length > 1) {
            fileName = args[0];
            targetFileName = args[1];
		} else {
			System.out.println("No file name specified.");
			return;
        }
        PowerPointReader reader = new PowerPointReader(fileName, targetFileName);
        reader.readPowerPoint();
	}

	public void readPPT() {
        // CoreProperties props = ppt.getProperties().getCoreProperties();
        printCode("        writer.createPowerPointFile();");
        
        int slideCount = 0;
        for (XSLFSlide slide: ppt.getSlides()) {
            printCode(" private void writeSlide" + slideCount++ + "(PowerPointReportWriter writer) {");
        	printCode("        writer.addSlide(null);");
        	List<XSLFShape> shapes = slide.getShapes();
        	for (XSLFShape shape: shapes) {

                // org.apache.poi.xslf.usermodel.XSLFAutoShape
                // org.apache.poi.xslf.usermodel.XSLFTextBox
                // org.apache.poi.xslf.usermodel.XSLFGraphicFrame
                // org.apache.poi.xslf.usermodel.XSLFGroupShape
                // org.apache.poi.xslf.usermodel.XSLFPictureShape
                // org.apache.poi.xslf.usermodel.XSLFTable

                if (shape instanceof XSLFAutoShape) {
                    XSLFAutoShape autoShape = (XSLFAutoShape)shape;
                    String prevAnchor = null;
                    // XSLFTextParagraph prevParagraph = null;
                    for(XSLFTextParagraph paragraph : autoShape.getTextParagraphs()) {
                        if(!rectToString(shape.getAnchor()).equals(prevAnchor)) {
                            // Sometimes, same paragrapths are added into same Shape. 
                            printCode("        writer.addPhraseAndTextRun(TextAlign." + paragraph.getTextAlign() + "," + rectToString(shape.getAnchor()) +", null);");
                        } else {
                            printCode("        writer.addLineBreaker();");
                        }
                        prevAnchor = rectToString(shape.getAnchor());
                        // prevParagraph = paragraph;
                        for(XSLFTextRun textRun : paragraph.getTextRuns()) {
                            String text = textRun.getRawText();
                            printCode("        writer.addTextRun(\"" + backSlash(text) + "\", " + textRun.getFontSize() + ", " + colorToString(((SolidPaint)textRun.getFontColor()).getSolidColor().getColor()) + ");");
                            //printCode("TextRun: Text[" + text + "] fontSize[" + textRun.getFontSize() + "] Color [" + textRun.getFontColor() + "]");
                        }
                    }
                    if(autoShape.getLineColor() != null) {
                        printCode("        writer.setLineColor(" + colorToString(autoShape.getLineColor()) + ");");
                    }
                } else if (shape instanceof XSLFTable) {
                    int colPosition;
                    XSLFTable table = (XSLFTable)shape;
                    printCode("        writer.addTable(" + rectToString(shape.getAnchor()) +");");
                    for(XSLFTableRow row: table.getRows()) {
                        printCode("        writer.addRow(" + row.getHeight() + ");");
                        colPosition = 0;
                        for(XSLFTableCell cell: row.getCells()) {
                            try {
                                printCode("        writer.addCell(" + table.getColumnWidth(colPosition++) + ");");
                            } catch (Exception e1) {
                                printCode("Error colPosition:" + colPosition);
                            }
                            makeTextCodes(cell, cell.getTextParagraphs());
                        }
                    }
                } else if (shape instanceof XSLFPictureShape) { 
                    System.out.println("PictureShape Anchor:" + rectToString(shape.getAnchor()));
                } else if (shape instanceof XSLFGroupShape) { 

                } else if (shape instanceof XSLFGraphicFrame) { 

                } else {
                    printCode(shape.getClass().getCanonicalName());
                    printCode("Shape Anchor:" + rectToString(shape.getAnchor()));
                }
            }	 
            printCode(" }");
        }
	}	
}