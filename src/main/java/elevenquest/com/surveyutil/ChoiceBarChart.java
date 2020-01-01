package elevenquest.com.surveyutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
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
    int maxRangeValue;
    
    public static Color BAR_COLOR1 = new Color(65, 152, 215);
    public static Color BAR_COLOR2 = new Color(245, 199, 103);
    public static Color BAR_COLOR3 = new Color(122, 97, 186);
    public static Color BAR_COLOR4 = new Color(57, 167, 103);
    public static Color BAR_COLOR5 = new Color(80, 70, 120);
    
    public static Color BAR_SHADOW_COLOR1 = new Color(32,76, 107);
    public static Color BAR_SHADOW_COLOR2 = new Color(122,100, 51);
    public static Color BAR_SHADOW_COLOR3 = new Color(61,49, 93);
    public static Color BAR_SHADOW_COLOR4 = new Color(29,83, 51);
    public static Color BAR_SHADOW_COLOR5 = new Color(40,35, 60);
    
    public static Color[] BAR_COLORS = {BAR_COLOR1, BAR_COLOR2, BAR_COLOR3, BAR_COLOR4, BAR_COLOR5 };
    public static Color[] BAR_SHADOW_COLORS = {BAR_SHADOW_COLOR1, BAR_SHADOW_COLOR2, BAR_SHADOW_COLOR3, BAR_SHADOW_COLOR4, BAR_SHADOW_COLOR5};
    
    public ChoiceBarChart(CategoryDataset dataset, String title, int maxRangeValue) {
    	this.dataset = dataset;
    	this.title = title;
    	this.maxRangeValue = maxRangeValue;
    }

    public ChoiceBarChart(SurveyQuestionResult result, int maxRangeValue) {
        this.result = result;
        dataset = createDataset();
        this.title = result.getTargetIdentity();
        this.maxRangeValue = maxRangeValue;
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
                "", // Category axis
                "", // Value axis
                dataset, PlotOrientation.VERTICAL, false, false, false);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(221,223,238));
        NumberAxis axis = (NumberAxis)plot.getRangeAxis();
        axis.setRange(0.0, maxRangeValue + 1);
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        System.out.println(plot.getRendererCount());
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        renderer.setSeriesPaint(0, BAR_COLOR1);
        renderer.setSeriesPaint(1, BAR_COLOR2);
        renderer.setSeriesPaint(2, BAR_COLOR3);
        renderer.setSeriesPaint(3, BAR_COLOR4);
        renderer.setBarPainter(new StandardBarPainter());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = chart.createBufferedImage(300,200);
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
        
        ChoiceBarChart chart = new ChoiceBarChart(dataset, "부장 홍길동", 15);
        chart.display();
    }

}