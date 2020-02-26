package uk.ac.tees.v8218996.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import uk.ac.tees.v8218996.trivia.data.AnswerListAsyncResponse;
import uk.ac.tees.v8218996.trivia.data.QuestionBank;
import uk.ac.tees.v8218996.trivia.model.Question;
import uk.ac.tees.v8218996.trivia.model.Score;
import uk.ac.tees.v8218996.trivia.util.Prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextview;
    private TextView questionCounterTextview;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private TextView scoreTextview;
    private TextView highscoreView;
    private int scoreCounter = 0;
    private Score score;

    private Prefs prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        score = new Score();

        prefs = new Prefs(MainActivity.this);

      //  Log.d("new score", "onCreate: "+prefs.getHighScore());

        highscoreView = findViewById(R.id.highest_score);
        scoreTextview = findViewById(R.id.score_counter_view);
        questionTextview = findViewById(R.id.questions_view);
        questionCounterTextview = findViewById(R.id.counter_text);
        nextButton = findViewById(R.id.next_btn);
        prevButton = findViewById(R.id.prev_btn);
        trueButton = findViewById(R.id.true_btn);
        falseButton=  findViewById(R.id.false_btn);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        currentQuestionIndex = prefs.getState();

       // AppController.getInstance()
        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextview.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionArrayList.size()));

               //Log.d("inside Async", "processFinished: "+ questionArrayList);
            }
        });

        scoreTextview.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        highscoreView.setText(MessageFormat.format("High Score: {0}", String.valueOf(prefs.getHighScore())));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.prev_btn:
                if(currentQuestionIndex > 0){
                    currentQuestionIndex = (currentQuestionIndex -1) % questionList.size();
                    updateQuestion();
                }

            break;
            case R.id.next_btn:

                goNextQuestion();
                break;

            case R.id.true_btn:

                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_btn:
                checkAnswer(false);
                updateQuestion();
                break;

        }
    }

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;

        if(userChooseCorrect == answerIsTrue){
            toastMessageId = R.string.correct_answer;
            fadeAnimation();
            addPoints();
        }else{
            toastMessageId = R.string.incorrect_answer;
            shakeAnimation();
            deductPoints();
        }

        Toast.makeText(MainActivity.this,toastMessageId,Toast.LENGTH_LONG).show();
    }

    private void addPoints(){
        scoreCounter +=100;

        score.setScore(scoreCounter);
        scoreTextview.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        Log.d("score", "addPoints: "+score.getScore());
    }
    private void deductPoints(){
        scoreCounter -=100;

        if(scoreCounter > 0){
            score.setScore(scoreCounter);

            scoreTextview.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        }else{
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextview.setText(MessageFormat.format("Current Score {0}", String.valueOf(score.getScore())));
            Log.d("score", "deductPoints: "+score.getScore());
        }


    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionCounterTextview.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size()));
        questionTextview.setText(question);
    }

    private void fadeAnimation(){

        final CardView cardView = findViewById(R.id.card_view);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(500);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);

        final CardView cardView = findViewById(R.id.card_view);

        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void goNextQuestion(){
        currentQuestionIndex = (currentQuestionIndex +1) % questionList.size();
        updateQuestion();
    }


    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }


}
