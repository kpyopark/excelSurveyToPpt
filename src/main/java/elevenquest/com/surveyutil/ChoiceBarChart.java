package elevenquest.com.surveyutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import elevenquest.com.SurveyQuestionResult;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.imageio.*;
import javax.swing.JFrame;


public class ChoiceBarChart {

    SurveyQuestionResult result;
    CategoryDataset dataset;
    String title;
    
    public ChoiceBarChart(CategoryDataset dataset, String title) {
    	this.dataset = dataset;
    	this.title = title;
    }

    public ChoiceBarChart(SurveyQuestionResult result) {
        this.result = result;
        dataset = createDataset();
        this.title = result.getTargetIdentity();
    }

    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Population in 2005
        result.getSurveyStatResult().forEach((key, value) -> {
            dataset.addValue(value, key, "Estim");
        });

        return dataset;
    }

    public byte[] exportAsPNG() throws IOException {
        JFreeChart chart = ChartFactory.createBarChart(null, // Chart Title
                this.title, // Category axis
                "Count #", // Value axis
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(221,223,238));
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        System.out.println(plot.getRendererCount());
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        renderer.setSeriesPaint(0, new Color(128,0,0));
        renderer.setSeriesPaint(1, new Color(0,128,0));
        renderer.setSeriesPaint(2, new Color(0,0,128));
        renderer.setSeriesPaint(3, new Color(64,64,64));
        renderer.setBarPainter(new StandardBarPainter());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = chart.createBufferedImage(80,120);
        ImageIO.write(image, "png", baos); 
        return baos.toByteArray();
    }
    
    public void display() throws Exception {
        JFreeChart chart = ChartFactory.createBarChart(null, // Chart Title
                "", // this.title , // Category axis
                "", // "Count #", // Value axis
                dataset, PlotOrientation.VERTICAL, false, false, false);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(221,223,238));
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        System.out.println(plot.getRendererCount());
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        renderer.setSeriesPaint(0, new Color(128,0,0));
        renderer.setSeriesPaint(1, new Color(0,128,0));
        renderer.setSeriesPaint(2, new Color(0,0,128));
        renderer.setSeriesPaint(3, new Color(64,64,64));
        renderer.setBarPainter(new StandardBarPainter());
        ChartPanel panel = new ChartPanel(chart);
        //panel.setSize(200,100);
        JFrame frame = new JFrame();
        frame.setContentPane(panel);
        frame.setSize(80,120);
        frame.setAlwaysOnTop(true);
        //frame.pack();
        frame.setVisible(true);
        Thread.sleep(10000);
    }
    
    public static void main(String[] args) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(10, "Good", "1");
        dataset.addValue(15, "Bad", "1");
        dataset.addValue(12, "Nice", "1");
        dataset.addValue(0, "N/A", "1");
        
        ChoiceBarChart chart = new ChoiceBarChart(dataset, "부장 홍길동");
        chart.display();
    }

}