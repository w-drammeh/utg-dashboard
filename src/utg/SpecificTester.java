package utg;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;

/**
 * This type is intended for testing a specific component / functionality of the project.
 * This class becomes useful when testing: for instance, how accurate are some computations?
 * Or how exactly are some components rendered before loading them up with the entire project.
 * E.g, to see how the Preview window looks like, use something like:
 *  SwingUtilities.invokeLater(()-> new Preview(null).setVisible(true));
 *
 * In whatever case, developer is assumed to be working on that particular side of the project.
 *
 * Never rely on this class for mass-testing! It does not load any data into memory, thus error-prone.
 */
public class SpecificTester {

    public static void main(String[] args) {
//        checkForUpdate();
        System.out.println(new Date());
    }

    public static void checkForUpdate(){
        final Document doc;
        try {
            doc = Jsoup.connect("https://github.com/w-drammeh/utg-dashboard").get();
            final Element version = doc.selectFirst(".markdown-body > p:nth-child(2) > code:nth-child(1)");
            System.out.println(version.text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
