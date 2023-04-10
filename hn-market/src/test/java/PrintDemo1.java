import com.spire.pdf.PdfDocument;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * @author majj
 * @create 2023-04-07 20:44
 */
public class PrintDemo1 {

    public static void main(String[] args) {

        //创建一个PrinterJob类的对象
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        //创建一个PageFormat类的对象，并将页面设置为默认尺寸和方向
        PageFormat pageFormat = printerJob.defaultPage();

        //创建Paper类的对象并获取纸张设置
        Paper paper = pageFormat.getPaper();

        //设置纸张的可绘制区域
        paper.setImageableArea(0, 0, pageFormat.getWidth(), pageFormat.getHeight());

        //应用纸张设置
        pageFormat.setPaper(paper);

        //创建一个PdfDocument类的对象
        PdfDocument pdf = new PdfDocument();

        //载入PDF文档
        pdf.loadFromFile("D:\\Chapter_10.pdf");

        //以设置的格式渲染文档页面
        printerJob.setPrintable(pdf, pageFormat);

        //执行打印
        try {
            printerJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
}
