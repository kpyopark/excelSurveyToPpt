package elevenquest.com.surveyutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import elevenquest.com.SurveyQuestionResult;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.imageio.*;


public class ChoiceBarChart {

    SurveyQuestionResult result;
    CategoryDataset dataset;

    public ChoiceBarChart(SurveyQuestionResult result) {
        this.result = result;
        dataset = createDataset();
    }

    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Population in 2005
        result.getSurveyStatResult().forEach((key, value) -> {
            dataset.addValue(value, "평가", key);
        });

        return dataset;
    }

    public byte[] exportAsPNG() throws IOException {
        JFreeChart chart = ChartFactory.createBarChart(null, // Chart Title
                this.result.getTargetIdentity() , // Category axis
                "Count #", // Value axis
                dataset, PlotOrientation.VERTICAL, true, true, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = chart.createBufferedImage(40,60);
        ImageIO.write(image, "png", baos); 
        return baos.toByteArray();
    }

}