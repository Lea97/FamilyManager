package dhbw.familymanager.familymanager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dhbw.familymanager.familymanager.model.Task;

public class XmlParser {

    XmlPullParserFactory factory;
    XmlPullParser parser;
    FileWriter writer;

    public XmlParser() {

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
        } catch (XmlPullParserException ex) {
            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(List<Task> allTasks, File file) throws IOException {

        writer = new FileWriter(file);
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        Task taskTmp;

        for (int i = 0; i < allTasks.size(); i++) {

            taskTmp = allTasks.get(i);
            String content = taskTmp.getTaskContent();
            boolean isDone = taskTmp.isDone();

            writer.write("<Task content=\"" + content + "\"" + " isdone=\"" + isDone + "\"" + ">");
            writer.write("</Task>");
        }

        writer.flush();

    }

    public List<Task> read(File file) throws XmlPullParserException, FileNotFoundException, IOException {
        parser.setInput(new FileReader(file));
        List<Task> list = new ArrayList<Task>();
        Task taskTmp;

        String content;
        boolean isDone;

        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

            if (parser.getEventType() == XmlPullParser.START_TAG) {

                content = parser.getAttributeValue(0);
                isDone = Boolean.parseBoolean(parser.getAttributeValue(1));
                taskTmp = new Task(content, isDone);
                list.add(taskTmp);
            }
            parser.next();
        }
        return list;
    }

}
