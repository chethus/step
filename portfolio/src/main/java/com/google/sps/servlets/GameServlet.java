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
import java.io.File;
import java.util.Base64;
import java.util.HashMap;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Scores of games currently in progress.
    private HashMap<Long, Integer> curScores = new HashMap<>();

    // Next answers for games currently in progress.
    private HashMap<Long, Integer> ans = new HashMap<>();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Load request params into strings.
        String startTimeStr = request.getParameter("start");
        String userAnsStr = request.getParameter("ans");
        String name = request.getParameter("name");

        // A post request must have a game start time and either an answer or username (for the highscore list).
        if (startTimeStr == null || (userAnsStr == null && name == null)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long startTime = Long.parseLong(startTimeStr);
        if (userAnsStr != null) {

            // Error if the game is over or if the start time is in the future (modulo some error).
            // Prevents hacking via extending game duration.
            if (isStartTimeIllegal(startTime)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            try {
                // Update the users score (they are identified by their game start time).
                int userAns = Integer.parseInt(userAnsStr);
                int correct = 0;
                if (userAns == ans.getOrDefault(startTime, Integer.MIN_VALUE)) {
                    correct = 1;
                }
                curScores.put(startTime, curScores.getOrDefault(startTime, 0) + correct);
            } catch (NumberFormatException e) {
                // If they didn't enter a number, no need to update scores.
            }

        // If name param exists, this is a request to be added to the highscore list.
        } else if (name != null) {

            // Create new entity on highscore list.
            Entity scoreEntity = new Entity("score");
            scoreEntity.setProperty("name", name);
            scoreEntity.setProperty("score", curScores.getOrDefault(startTime, 0));
            datastore.put(scoreEntity);

            // Remove entries from HashMaps to keep them small.
            curScores.remove(startTime);
            ans.remove(startTime);
        }

        // Send back the user's updated score.
        response.setContentType("text/plain");
        response.getWriter().println(curScores.getOrDefault(startTime, 0));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        // Required to fix an obscure font config bug.
        // See https://stackoverflow.com/questions/45100138/how-to-configure-google-appengine-to-work-with-vector-graphic.
        String fontConfig = System.getProperty("java.home")
            + File.separator + "lib"
            + File.separator + "fontconfig.Prodimage.properties";
        if (new File(fontConfig).exists()) {
            System.setProperty("sun.awt.fontconfig", fontConfig);
        }

        // Require start time so we know what client we are sending the question to.
        String startTimeStr = request.getParameter("start");
        if (startTimeStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Check if the start time is invalid.
        long startTime = Long.parseLong(startTimeStr);
        if (isStartTimeIllegal(startTime)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Write back the question as a Base64 image (to prevent extraction of the question text).
        response.setContentType("text/plain");
        response.getWriter().println(textToBase64(getQuestion(startTime)));
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
                a = (int) (Math.random() * 999 + 1);
                b = (int) (Math.random() * 999 + 1);
                question = a + " + " + b;
                ans.put(startTime, a + b);
                break;
            case 1:
                a = (int) (Math.random() * 999 + 1);
                b = (int) (Math.random() * 999 + 1);
                question = a + " - " + b;
                ans.put(startTime, a - b);
                break;
            default:
                a = (int) (Math.random() * 99 + 1);
                b = (int) (Math.random() * 99 + 1);
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

    private boolean isStartTimeIllegal(long startTime) {
        long time = System.currentTimeMillis();
        return time - startTime > 60500 || time - startTime < -50;
    }
}