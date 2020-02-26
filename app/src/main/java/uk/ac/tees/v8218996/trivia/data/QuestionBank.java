package uk.ac.tees.v8218996.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import uk.ac.tees.v8218996.trivia.controller.AppController;
import uk.ac.tees.v8218996.trivia.model.Question;

import static uk.ac.tees.v8218996.trivia.controller.AppController.TAG;

public class QuestionBank {

    ArrayList <Question> questionArrayList = new ArrayList<>();
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";


    public List<Question> getQuestions(final AnswerListAsyncResponse callBack){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                       // Log.d(TAG, "onResponse: " + response);

                        for (int i = 0; i<response.length(); i++){
                            try {
                                Question question = new Question();
                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                                questionArrayList.add(question);
                                //Log.d("Hello", "onResponse: " +question);
                                //getting the first item or index on the array (question)
                                //response.getJSONArray(i).get(0);
                                //getting the second item or index on the array (answers)
                                //response.getJSONArray(i).getBoolean(1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //Log.d("l", "getQuestions: " + questionArrayList);

                        if(null !=callBack) callBack.processFinished(questionArrayList);

                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        //Log.d("l", "getQuestions: " + questionArrayList);
        return questionArrayList;
    }
}
