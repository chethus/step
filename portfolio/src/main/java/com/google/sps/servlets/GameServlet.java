import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@WebServlet("/question")
public class QuestionServlet extends HttpServlet {
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private HashMap<Long, Integer> curScores = new HashMap<>();
    private HashMap<Long, Integer> ans;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String startTimeStr = request.getParameter("start");
        String userAnsStr = request.getParameter("ans");
        String name = request.getParameter("name");
        if (startTimeStr == null || (userAnsStr == null && name == null)) {
            response.setError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        long startTime = Long.parseLong(startTimeStr);
        long time = System.currentTimeMillis();
        if (time - startTime > 60000 || time - startTime < 0) {
            response.setError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (userAnsStr != null) {
            try {
                int userAns = Integer.parseInt(userAnsStr);
                int correct = 0;
                if (userAns == ans.getOrDefault(startTime, Integer.MIN_VALUE)) {
                    corect = 1;
                }
                curScores.put(curScores.getOrDefault(startTime, 0) + correct);
            } catch (NumberFormatException e) {
            }
        } else if (name != null) {
            Entity score = new Entity("score");
            entity.setProperty("name", name);
            entity.setProperty("score", curScores.get(startTime, 0));
            datastore.put(score);
        }
        response.setContentType("text/plain");
        response.getWriter().println(curScores.get(startTime, 0));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String startTimeStr = request.getParameter("start");
        if (startTimeStr == null) {
            response.setError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        long startTime = Long.parseLong(startTimeStr);
        long time = System.currentTimeMillis();
        if (time - startTime > 60000 || time - startTime < 0) {
            response.setError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType("text/plain");
        response.getWriter().println(Base64(getQuestion(startTime)));
    }

    // Generate a new question and set the answer instance variable.
    public String getQuestion(long startTime) {
        
        // Randomly select image type.
        int type = (int) (Math.random() * 3);
        
        // Create addition, subtraction, or multiplication question.

        String question = null;
        int a, b;
        switch (type) {

            case 0:
                a = (int) (Math.random() * 1000);
                b = (int) (Math.random() * 1000);
                question = a + " + " + b;
                ans.put(startTime, a + b);
                break;
            case 1:
                a = (int) (Math.random() * 1000);
                b = (int) (Math.random() * 1000);
                question = a + " - " + b;
                ans.put(startTime, a - b);
                break;
            default:
                a = (int) (Math.random() * 100);
                b = (int) (Math.random() * 100);
                question = a + " * " + b;
                ans.put(startTime, a * b);
                break;
        }
        
        return question;
    }
    
    // Convert question text to Base64 image string.
    public static String textToBase64(String text) throws IOException{
        BufferedImage bufferedImage = new BufferedImage(200, 60, BufferedImage.TYPE_BYTE_GRAY);

        // Write problem to image.
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 200, 60);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.PLAIN, 35));
        graphics.drawString(text, 10, 50);

        // Convert image to Base64.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", out);
        byte[] bytes = out.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}