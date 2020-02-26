package uk.ac.tees.v8218996.trivia.data;

import java.util.ArrayList;

import uk.ac.tees.v8218996.trivia.model.Question;

public interface AnswerListAsyncResponse {

    void processFinished(ArrayList<Question> questionArrayList);

}
