import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;

import com.google.sps.data.Score;

@WebServlet("/scores")
public class ScoreServlet extends HttpServlet {
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        
        // Get score limit parameter.
        int max = Integer.parseInt(request.getParameter("max"));

        // Get page number.
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        ArrayList<Score> scores = new ArrayList<>();

        // Create a query for the Scores.
        Query query = new Query("score").addSort("score", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        // Skip earlier pages and enforce Score limit.
        FetchOptions options = FetchOptions.Builder.withLimit(max);
        options.offset((page - 1) * max);

        int rank = 1;
        for (Entity entity : results.asIterable(options)) {
            scores.add(Score.makeScore(entity, rank));
            rank ++;
        }

        // Send JSON back to site.
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(scores));
    }
}